package ru.spring.controller;

import commentservice.CommentOuterClass;
import commentservice.CommentServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.web.bind.annotation.*;
import ru.spring.dto.commentservice.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private ManagedChannel commentChannel;
    private CommentServiceGrpc.CommentServiceBlockingStub commentStub;

    @PostConstruct
    public void init() {
        commentChannel = ManagedChannelBuilder.forTarget("commentservice:50051").usePlaintext().build();
        commentStub = CommentServiceGrpc.newBlockingStub(commentChannel);
    }

    @PreDestroy
    public void shutdown() {
        if (commentChannel != null && !commentChannel.isShutdown()) {
            commentChannel.shutdownNow();
        }
    }


    @PostMapping()
    public CreateCommentResponseDto createComment(@RequestBody CreateCommentRequestDto requestDTO) {
        CommentOuterClass.CommentPostRequest request = CommentOuterClass.CommentPostRequest.newBuilder()
                .setPostId(requestDTO.getPostId())
                .setUserId(requestDTO.getUserId())
                .setContent(requestDTO.getContent())
                .build();

        CommentOuterClass.CommentPostResponse response = commentStub.commentPost(request);
        CreateCommentResponseDto createCommentResponseDto = new CreateCommentResponseDto();
        createCommentResponseDto.setCommentId(response.getCommentId());
        createCommentResponseDto.setSuccess(response.getSuccess());
        return createCommentResponseDto;
    }

    @PostMapping("/like")
    public ViewLikeResponseDto likeComment(@RequestBody ViewLikeRequestDto requestDTO) {
        CommentOuterClass.LikePostRequest request = CommentOuterClass.LikePostRequest.newBuilder()
                .setPostId(requestDTO.getPostId())
                .setUserId(requestDTO.getUserId())
                .build();

        CommentOuterClass.LikePostResponse response = commentStub.likePost(request);
        ViewLikeResponseDto viewLikeResponseDto = new ViewLikeResponseDto();
        viewLikeResponseDto.setCount(response.getLikeCounts());
        viewLikeResponseDto.setSuccess(response.getSuccess());
        return viewLikeResponseDto;
    }

    @PostMapping("/view")
    public ViewLikeResponseDto viewComment(@RequestBody ViewLikeRequestDto requestDTO) {
        CommentOuterClass.ViewPostRequest request = CommentOuterClass.ViewPostRequest.newBuilder()
                .setPostId(requestDTO.getPostId())
                .setUserId(requestDTO.getUserId())
                .build();

        CommentOuterClass.ViewPostResponse response = commentStub.viewPost(request);
        ViewLikeResponseDto viewLikeResponseDto = new ViewLikeResponseDto();
        viewLikeResponseDto.setSuccess(response.getSuccess());
        viewLikeResponseDto.setCount(response.getViewsCount());
        return viewLikeResponseDto;
    }

    @GetMapping()
    public ListCommentResponseDto listComments(
            @RequestParam(name = "postId") String postId,
            @RequestParam(name = "page",     defaultValue = "0") int page,
            @RequestParam(name = "pageSize", defaultValue = "5") int pageSize
    ) {
        // 1) Формируем gRPC-запрос
        CommentOuterClass.ListCommentsRequest request = CommentOuterClass.ListCommentsRequest.newBuilder()
                .setPostId(postId)
                .setPageToken(String.valueOf(page))
                .setPageSize(pageSize)
                .build();

        // 2) Вызываем gRPC-метод
        CommentOuterClass.ListCommentsResponse grpcResp = commentStub.listComments(request);

        // 3) Конвертируем в REST-DTO
        List<CommentDto> comments = grpcResp.getCommentsList().stream().map(c -> {
            CommentDto dto = new CommentDto();
            dto.setCommentId(c.getCommentId());
            dto.setPostId(c.getPostId());
            dto.setUserId(c.getUserId());
            dto.setContent(c.getContent());
            long millis = c.getTimestamp().getSeconds() * 1_000L
                    + c.getTimestamp().getNanos() / 1_000_000;
            dto.setTimestamp(millis);
            return dto;
        }).toList();

        ListCommentResponseDto response = new ListCommentResponseDto();
        response.setComments(comments);
        response.setNextPageToken(grpcResp.getNextPageToken());
        return response;
    }
}
