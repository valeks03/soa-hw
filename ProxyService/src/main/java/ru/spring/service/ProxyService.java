package ru.spring.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;
import com.example.social_network.PostServiceGrpc;

//Тут будут вызываться методы gRPC

@Service
public class ProxyService {

    ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50050").usePlaintext().build();
    PostServiceGrpc.PostServiceBlockingStub postService = PostServiceGrpc.newBlockingStub(channel);

}
