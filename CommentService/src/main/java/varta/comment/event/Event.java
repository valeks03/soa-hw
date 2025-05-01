package varta.comment.event;

import java.time.Instant;

public class Event {
    String user_Id;
    String post_Id;
    long timestamp ;

    public Event(String user_Id, String post_Id) {
        this.user_Id = user_Id;
        this.post_Id = post_Id;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public String getUser_Id() {
        return user_Id;
    }
    public String getPost_Id() {
        return post_Id;
    }
    public long getTimestamp() {
        return timestamp;
    }
}
