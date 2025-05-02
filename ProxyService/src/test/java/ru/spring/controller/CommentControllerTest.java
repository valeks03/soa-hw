package ru.spring.controller;

import commentservice.CommentOuterClass;
import commentservice.CommentServiceGrpc;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CommentControllerTest {

    private MockMvc mockMvc;
    private CommentController controller;
    private CommentServiceGrpc.CommentServiceBlockingStub stub;

    @BeforeEach
    void setUp() {
        // создаём контроллер «вручную»
        controller = new CommentController();

        // мок для gRPC-стаба
        stub = mock(CommentServiceGrpc.CommentServiceBlockingStub.class);

        // внедряем его через ReflectionTestUtils (postConstruct тут не сработает)
        ReflectionTestUtils.setField(controller, "commentStub", stub);

        // channel не важен, но чтобы @PreDestroy не NPE, запомним мок
        ReflectionTestUtils.setField(controller, "commentChannel", mock(ManagedChannel.class));

        // создаём standalone MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createComment_shouldReturnDto() throws Exception {
        // подготавливаем ответ gRPC
        CommentOuterClass.CommentPostResponse grpcResp =
                CommentOuterClass.CommentPostResponse.newBuilder()
                        .setSuccess(true)
                        .setCommentId("c123")
                        .build();
        when(stub.commentPost(any())).thenReturn(grpcResp);

        // вызываем REST
        mockMvc.perform(post("/comment")
                        .contentType(APPLICATION_JSON)
                        .content("""
                    {
                      "postId":"p1",
                      "userId":"u1",
                      "content":"hello"
                    }
                    """
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.commentId").value("c123"));

        verify(stub).commentPost(any());
    }

    @Test
    void likeComment_shouldReturnCountAndSuccess() throws Exception {
        // подготавливаем ответ gRPC
        CommentOuterClass.LikePostResponse grpcResp =
                CommentOuterClass.LikePostResponse.newBuilder()
                        .setSuccess(true)
                        .setLikeCounts(5)
                        .build();
        when(stub.likePost(any())).thenReturn(grpcResp);

        mockMvc.perform(post("/comment/like")
                        .contentType(APPLICATION_JSON)
                        .content("""
                    {
                      "postId":"p2",
                      "userId":"u2"
                    }
                    """
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(5));

        verify(stub).likePost(any());
    }

    @Test
    void viewComment_shouldReturnViewsCount() throws Exception {
        // подготавливаем ответ gRPC
        CommentOuterClass.ViewPostResponse grpcResp =
                CommentOuterClass.ViewPostResponse.newBuilder()
                        .setSuccess(true)
                        .setViewsCount(42)
                        .build();
        when(stub.viewPost(any())).thenReturn(grpcResp);

        mockMvc.perform(post("/comment/view")
                        .contentType(APPLICATION_JSON)
                        .content("""
                    {
                      "postId":"p3",
                      "userId":"u3"
                    }
                    """
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(42));

        verify(stub).viewPost(any());
    }

    @Test
    void listComments_shouldReturnCommentsAndNextToken() throws Exception {
        // собираем пару CommentProto.Comment
        CommentOuterClass.Comment c1 = CommentOuterClass.Comment.newBuilder()
                .setCommentId("c1")
                .setPostId("p")
                .setUserId("u")
                .setContent("hey")
                .setTimestamp(
                        com.google.protobuf.Timestamp.newBuilder()
                                .setSeconds(1)
                                .setNanos(500_000_000)
                                .build()
                )
                .build();
        CommentOuterClass.Comment c2 = CommentOuterClass.Comment.newBuilder()
                .setCommentId("c2")
                .setPostId("p")
                .setUserId("u")
                .setContent("ho")
                .setTimestamp(
                        com.google.protobuf.Timestamp.newBuilder()
                                .setSeconds(2)
                                .setNanos(0)
                                .build()
                )
                .build();

        CommentOuterClass.ListCommentsResponse grpcResp =
                CommentOuterClass.ListCommentsResponse.newBuilder()
                        .addComments(c1)
                        .addComments(c2)
                        .setNextPageToken("2")
                        .build();
        when(stub.listComments(any())).thenReturn(grpcResp);

        mockMvc.perform(get("/comment")
                        .param("postId", "p")
                        .param("page", "0")
                        .param("pageSize", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments[0].commentId").value("c1"))
                .andExpect(jsonPath("$.comments[0].content").value("hey"))
                .andExpect(jsonPath("$.comments[1].commentId").value("c2"))
                .andExpect(jsonPath("$.nextPageToken").value("2"));

        verify(stub).listComments(any());
    }
}