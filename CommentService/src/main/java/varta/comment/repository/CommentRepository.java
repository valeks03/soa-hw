package varta.comment.repository;

import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Repository;
import varta.comment.event.Comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CommentRepository {
    private final ConcurrentHashMap<String, List<Comment>> comments = new ConcurrentHashMap<>();


    public String addComment(String post_id, String user_id, String content) {
        String comment_id = UUID.randomUUID().toString();
        Comment comment = new Comment(comment_id, post_id, user_id, content);

        List<Comment> list = comments.computeIfAbsent(post_id, k -> Collections.synchronizedList(new ArrayList<>()));
        list.add(comment);
        return comment_id;
    }

    public List<Comment> getComments(String postId) {
        List<Comment> list = comments.getOrDefault(postId, Collections.emptyList());
        synchronized (list) {
            return new ArrayList<>(list);
        }
    }

    public List<Comment> getCommentsPage(String postId, int page, int size) {
        List<Comment> list = comments.getOrDefault(postId, Collections.emptyList());
        List<Comment> snapshot;
        synchronized (list) {
            snapshot = new ArrayList<>(list);
        }
        //нумерация страниц идет с нулевой
        int from = page * size;
        if (from >= snapshot.size()) {
            return Collections.emptyList();
        }
        int to = Math.min(from + size, snapshot.size());
        return snapshot.subList(from, to);
    }

}
