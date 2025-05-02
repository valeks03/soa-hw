package ru.spring.dto.commentservice;

public class CreateCommentRequestDto {
    String postId;
    String userId;
    String content;

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getUserId() {
        return userId;
    }

    public String getPostId() {
        return postId;
    }
}
