package ru.spring.dto.commentservice;

public class CreateCommentResponseDto {
    private boolean success;
    private String commentId;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
