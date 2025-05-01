package varta.comment.controller;

import commentservice.CommentOuterClass;
import commentservice.CommentOuterClass.LikePostRequest;
import commentservice.CommentOuterClass.LikePostResponse;
import commentservice.CommentOuterClass.CommentPostRequest;
import commentservice.CommentOuterClass.CommentPostResponse;
import commentservice.CommentOuterClass.ListCommentsRequest;
import commentservice.CommentOuterClass.ListCommentsResponse;
import commentservice.CommentOuterClass.ViewPostRequest;
import commentservice.CommentOuterClass.ViewPostResponse;

import commentservice.CommentServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.kafka.core.KafkaTemplate;
import varta.comment.event.Comment;
import varta.comment.event.Event;
import varta.comment.repository.CommentRepository;
import varta.comment.repository.LikeRepository;
import varta.comment.repository.ViewRepository;

import java.util.List;
import java.util.UUID;

@GrpcService
public class CommentServiceImp extends CommentServiceGrpc.CommentServiceImplBase {

    private final KafkaTemplate<String, Event> kafkaTemplate;
    private final LikeRepository likeRepo;
    private final ViewRepository viewRepo;
    private final CommentRepository commentRepo;

    public CommentServiceImp(KafkaTemplate<String, Event> kafkaTemplate, ViewRepository viewRepo, LikeRepository likeRepo, CommentRepository commentRepo) {
        this.kafkaTemplate = kafkaTemplate;
        this.likeRepo = likeRepo;
        this.viewRepo = viewRepo;
        this.commentRepo = commentRepo;
    }


    @Override
    public void likePost(LikePostRequest request, StreamObserver<LikePostResponse> responseObserver) {
        String eventId = UUID.randomUUID().toString();
        Event event = new Event(request.getUserId(), request.getPostId());
        kafkaTemplate.send("like-event", eventId, event);

        //эта штука работает асинхронно
        int numberLikes = likeRepo.addLike(request.getUserId(), request.getPostId());

        LikePostResponse response = LikePostResponse.newBuilder()
                .setSuccess(true)
                .setLikeCounts(numberLikes)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void viewPost(ViewPostRequest request, StreamObserver<ViewPostResponse> responseObserver) {
        String viewId = UUID.randomUUID().toString();
        Event event = new Event(request.getUserId(), request.getPostId());
        kafkaTemplate.send("view-event", viewId, event);


        int numberViews = viewRepo.addView(request.getUserId(), request.getPostId());
        ViewPostResponse response = ViewPostResponse.newBuilder()
                .setSuccess(true)
                .setViewsCount(numberViews)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void commentPost(CommentPostRequest request, StreamObserver<CommentPostResponse> responseObserver) {
        String eventId = UUID.randomUUID().toString();
        Event event = new Event(request.getUserId(), request.getPostId());
        kafkaTemplate.send("comment-event", eventId, event);

        String commentId = commentRepo.addComment(request.getPostId(), request.getUserId(), request.getContent());

        CommentPostResponse response = CommentPostResponse.newBuilder()
                .setSuccess(true)
                .setCommentId(commentId)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listComments(ListCommentsRequest request, StreamObserver<ListCommentsResponse> responseObserver) {
        String postId = request.getPostId();
        int pageSize = request.getPageSize();
        // токен в формате числа страницы, если пустой — начинаем с 0
        int page = 0;
        if (request.getPageToken() != null && !request.getPageToken().isEmpty()) {
            try {
                page = Integer.parseInt(request.getPageToken());
            } catch (NumberFormatException e) {
                // неверный токен — можно вернуть ошибку, но для простоты — вернуть пустой результат
                responseObserver.onError(
                        Status.INVALID_ARGUMENT
                                .withDescription("Invalid page token: " + request.getPageToken())
                                .asRuntimeException()
                );
                return;
            }
        }

        // получаем «страницу» комментариев из репозитория
        List<Comment> pageList = commentRepo.getCommentsPage(postId, page, pageSize);

        // строим ответ
        ListCommentsResponse.Builder respB = ListCommentsResponse.newBuilder();
        for (Comment c : pageList) {
            respB.addComments(
                    CommentOuterClass.Comment.newBuilder()
                            .setCommentId(c.getComment_id())
                            .setPostId(c.getPost_id())
                            .setUserId(c.getUser_id())
                            .setContent(c.getContent())
                            .setTimestamp(
                                    com.google.protobuf.Timestamp.newBuilder()
                                            .setSeconds(c.getTimestamp() / 1000)
                                            .setNanos((int)((c.getTimestamp() % 1000) * 1_000_000))
                                            .build()
                            )
                            .build()
            );
        }

        // если элементов ровно pageSize — есть следующая страница
        String nextToken = "";
        if (pageList.size() == pageSize) {
            nextToken = String.valueOf(page + 1);
        }
        respB.setNextPageToken(nextToken);

        responseObserver.onNext(respB.build());
        responseObserver.onCompleted();
    }
}
