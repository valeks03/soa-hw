package varta.comment;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import varta.comment.controller.CommentServiceImp;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException{

        Server server = ServerBuilder
                .forPort(50051)
                .addService(new CommentServiceImp())
                .build();


        server.start();
        System.out.println("Server started, listening on " + server.getPort());
        server.awaitTermination();
    }
}