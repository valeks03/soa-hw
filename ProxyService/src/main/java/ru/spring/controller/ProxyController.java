package ru.spring.controller;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.web.bind.annotation.*;
import com.example.social_network.PostOuterClass;
import com.example.social_network.PostServiceGrpc;

import ru.spring.dto.postservice.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/posts")
public class ProxyController {

    private ManagedChannel channel;
    private PostServiceGrpc.PostServiceBlockingStub stub;

    @PostConstruct
    public void init() {
        channel = ManagedChannelBuilder.forTarget("postservice:50050").usePlaintext().build();
        stub = PostServiceGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdownNow();
        }
    }

    @PostMapping
    public PostResponseDTO createPost(@RequestBody CreatePostRequestDTO requestDTO) {
        PostOuterClass.CreatePostRequest grpcRequest = PostOuterClass.CreatePostRequest.newBuilder()
                .setTitle(requestDTO.getTitle())
                .setDescription(requestDTO.getDescription())
                .setCreatorId(requestDTO.getCreatorId())
                .setIsPrivate(requestDTO.isPrivate())
                .addAllTags(requestDTO.getTags())
                .build();

        PostOuterClass.CreatePostResponse grpcResponse = stub.createPost(grpcRequest);
        return convertToDto(grpcResponse.getPost());
    }

    @GetMapping("/{id}")
    public PostResponseDTO getPostById(@PathVariable("id") String id) {
        PostOuterClass.GetPostByIdRequest grpcRequest = PostOuterClass.GetPostByIdRequest
                .newBuilder()
                .setId(id)
                .build();
        PostOuterClass.GetPostByIdResponse grpcResponse = stub.getPostById(grpcRequest);
        return convertToDto(grpcResponse.getPost());
    }


    @GetMapping
    public GetPostsResponseDTO getPosts(@RequestParam(name = "page", defaultValue = "1") int page,
                                        @RequestParam(name = "pageSize", defaultValue = "5") int pageSize) {
        PostOuterClass.GetPostsRequest grpcRequest = PostOuterClass.GetPostsRequest.newBuilder()
                .setPage(page)
                .setPageSize(pageSize)
                .build();
        PostOuterClass.GetPostsResponse grpcResponse = stub.getPosts(grpcRequest);

        List<PostResponseDTO> posts = grpcResponse.getPostsList().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());


        GetPostsResponseDTO responseDto = new GetPostsResponseDTO();
        responseDto.setPosts(posts);
        responseDto.setTotalCount(grpcResponse.getTotalCount());
        responseDto.setPage(grpcResponse.getPage());
        responseDto.setPageSize(grpcResponse.getPageSize());
        return responseDto;

    }

    @PutMapping
    public PostResponseDTO updatePost(@RequestBody UpdatePostRequestDTO requestDTO) {
        PostOuterClass.UpdatePostRequest grpcRequest = PostOuterClass.UpdatePostRequest
                .newBuilder()
                .setId(requestDTO.getId())
                .setTitle(requestDTO.getTitle() != null ? requestDTO.getTitle() : "")
                .setDescription(requestDTO.getDescription() != null ? requestDTO.getDescription() : "")
                .setIsPrivate(requestDTO.isPrivate())
                .addAllTags(requestDTO.getTags())
                .build();

        PostOuterClass.UpdatePostResponse grpcResponse = stub.updatePost(grpcRequest);
        return convertToDto(grpcResponse.getPost());
    }

    @DeleteMapping("/{id}")
    public DeletePostResponseDTO deletePost(@PathVariable("id") String id) {
       PostOuterClass.DeletePostRequest grpcRequest = PostOuterClass.DeletePostRequest
               .newBuilder()
               .setId(id)
               .build();
       PostOuterClass.DeletePostResponse grpcResponse =  stub.deletePost(grpcRequest);

       DeletePostResponseDTO responseDto = new DeletePostResponseDTO();
       responseDto.setSuccess(grpcResponse.getSuccess());
       return responseDto;
    }


    // Метод для преобразования protobuf-объекта Post в DTO
    private PostResponseDTO convertToDto(PostOuterClass.Post post) {
        PostResponseDTO dto = new PostResponseDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setCreatorId(post.getCreatorId());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setPrivate(post.getIsPrivate());
        dto.setTags(post.getTagsList());
        return dto;
    }
}
