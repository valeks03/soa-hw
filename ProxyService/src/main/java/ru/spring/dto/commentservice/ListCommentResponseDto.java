package ru.spring.dto.commentservice;


import java.util.List;

public class ListCommentResponseDto {
    private List<CommentDto> comments;
    private String nextPageToken;

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }
}
