package varta.comment.event;

import java.time.Instant;

public class Comment {
    String comment_id;
    String post_id;
    String user_id;
    String content;
    long timestamp;

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public Comment(String comment_id, String post_id, String user_id, String content) {
        this.comment_id = comment_id;
        this.post_id = post_id;
        this.user_id = user_id;
        this.content = content;
        this.timestamp = Instant.now().toEpochMilli();
    }

}
