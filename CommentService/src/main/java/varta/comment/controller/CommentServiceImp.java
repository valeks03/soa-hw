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
import io.grpc.stub.StreamObserver;


public class CommentServiceImp extends CommentServiceGrpc.CommentServiceImplBase {

    public void likePost(LikePostRequest request, StreamObserver<LikePostResponse> responseObserver) {

    }

    public void viewPost(ViewPostRequest request, StreamObserver<ViewPostResponse> responseObserver) {

    }

    public void commentPost(CommentPostRequest request, StreamObserver<CommentPostResponse> responseObserver) {

    }
    public void listComments(ListCommentsRequest request, StreamObserver<ListCommentsResponse> responseObserver) {

    }
}
