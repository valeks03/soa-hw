package ru.spring.controller;

import com.example.social_network.PostOuterClass;
import com.example.social_network.PostServiceGrpc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import ru.spring.dto.CreatePostRequestDTO;
import ru.spring.dto.UpdatePostRequestDTO;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProxyController.class)
public class ProxyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProxyController proxyController;

    @Autowired
    private ObjectMapper objectMapper;

    // Создадим mock для gRPC-стаба
    private PostServiceGrpc.PostServiceBlockingStub stubMock;

    @BeforeEach
    public void setUp() {
        stubMock = Mockito.mock(PostServiceGrpc.PostServiceBlockingStub.class);
        // Заменяем приватное поле stub в ProxyController на наш mock
        ReflectionTestUtils.setField(proxyController, "stub", stubMock);
    }

    @Test
    public void testCreatePost() throws Exception {
        PostOuterClass.Post grpcPost = PostOuterClass.Post.newBuilder()
                .setId("1")
                .setTitle("Test Title")
                .setDescription("Test description")
                .setCreatorId("123")
                .setIsPrivate(false)
                .addAllTags(Arrays.asList("tag1", "tag2"))
                .build();
        PostOuterClass.CreatePostResponse grpcResponse = PostOuterClass.CreatePostResponse.newBuilder()
                .setPost(grpcPost)
                .build();

        when(stubMock.createPost(any(PostOuterClass.CreatePostRequest.class))).thenReturn(grpcResponse);

        // Подготавливаем DTO запроса
        CreatePostRequestDTO requestDTO = new CreatePostRequestDTO();
        requestDTO.setTitle("Test Title");
        requestDTO.setDescription("Test description");
        requestDTO.setCreatorId("123");
        requestDTO.setPrivate(false);
        requestDTO.setTags(Arrays.asList("tag1", "tag2"));

        String requestJson = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.description").value("Test description"))
                .andExpect(jsonPath("$.creatorId").value("123"))
                .andExpect(jsonPath("$.private").value(false))
                .andExpect(jsonPath("$.tags[0]").value("tag1"))
                .andExpect(jsonPath("$.tags[1]").value("tag2"));
    }

    @Test
    public void testGetPostById() throws Exception {
        PostOuterClass.Post grpcPost = PostOuterClass.Post.newBuilder()
                .setId("2")
                .setTitle("Get Title")
                .setDescription("Get description")
                .setCreatorId("456")
                .setIsPrivate(true)
                .addAllTags(Arrays.asList("tagA", "tagB"))
                .build();
        PostOuterClass.GetPostByIdResponse grpcResponse = PostOuterClass.GetPostByIdResponse.newBuilder()
                .setPost(grpcPost)
                .build();

        when(stubMock.getPostById(any(PostOuterClass.GetPostByIdRequest.class))).thenReturn(grpcResponse);

        mockMvc.perform(get("/posts/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.title").value("Get Title"))
                .andExpect(jsonPath("$.description").value("Get description"))
                .andExpect(jsonPath("$.creatorId").value("456"))
                .andExpect(jsonPath("$.private").value(true))
                .andExpect(jsonPath("$.tags[0]").value("tagA"))
                .andExpect(jsonPath("$.tags[1]").value("tagB"));
    }

    @Test
    public void testGetPosts() throws Exception {
        PostOuterClass.Post grpcPost1 = PostOuterClass.Post.newBuilder()
                .setId("1")
                .setTitle("Title 1")
                .setDescription("Description 1")
                .setCreatorId("123")
                .setIsPrivate(false)
                .addAllTags(Arrays.asList("tag1"))
                .build();

        PostOuterClass.Post grpcPost2 = PostOuterClass.Post.newBuilder()
                .setId("2")
                .setTitle("Title 2")
                .setDescription("Description 2")
                .setCreatorId("456")
                .setIsPrivate(true)
                .addAllTags(Arrays.asList("tag2"))
                .build();

        PostOuterClass.GetPostsResponse grpcResponse = PostOuterClass.GetPostsResponse.newBuilder()
                .addAllPosts(Arrays.asList(grpcPost1, grpcPost2))
                .setTotalCount(2)
                .setPage(1)
                .setPageSize(5)
                .build();

        when(stubMock.getPosts(any(PostOuterClass.GetPostsRequest.class))).thenReturn(grpcResponse);

        mockMvc.perform(get("/posts")
                        .param("page", "1")
                        .param("pageSize", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(2))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.pageSize").value(5))
                .andExpect(jsonPath("$.posts[0].id").value("1"))
                .andExpect(jsonPath("$.posts[0].title").value("Title 1"))
                .andExpect(jsonPath("$.posts[1].id").value("2"))
                .andExpect(jsonPath("$.posts[1].title").value("Title 2"));
    }

    @Test
    public void testUpdatePost() throws Exception {
        PostOuterClass.Post grpcPost = PostOuterClass.Post.newBuilder()
                .setId("3")
                .setTitle("Updated Title")
                .setDescription("Updated description")
                .setCreatorId("789")
                .setIsPrivate(false)
                .addAllTags(Arrays.asList("tagX", "tagY"))
                .build();

        PostOuterClass.UpdatePostResponse grpcResponse = PostOuterClass.UpdatePostResponse.newBuilder()
                .setPost(grpcPost)
                .build();

        when(stubMock.updatePost(any(PostOuterClass.UpdatePostRequest.class))).thenReturn(grpcResponse);

        UpdatePostRequestDTO requestDTO = new UpdatePostRequestDTO();
        requestDTO.setId("3");
        requestDTO.setTitle("Updated Title");
        requestDTO.setDescription("Updated description");
        requestDTO.setPrivate(false);
        requestDTO.setTags(Arrays.asList("tagX", "tagY"));

        String requestJson = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(put("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("3"))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.creatorId").value("789"))
                .andExpect(jsonPath("$.private").value(false))
                .andExpect(jsonPath("$.tags[0]").value("tagX"))
                .andExpect(jsonPath("$.tags[1]").value("tagY"));
    }

    @Test
    public void testDeletePost() throws Exception {
        PostOuterClass.DeletePostResponse grpcResponse = PostOuterClass.DeletePostResponse.newBuilder()
                .setSuccess(true)
                .build();

        when(stubMock.deletePost(any(PostOuterClass.DeletePostRequest.class))).thenReturn(grpcResponse);

        mockMvc.perform(delete("/posts/4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}