package varta.comment.repository;

import java.util.concurrent.ConcurrentHashMap;

public class CommentRepository {
    private final ConcurrentHashMap viewRepository = new ConcurrentHashMap<>();
    private final ConcurrentHashMap likeRepository = new ConcurrentHashMap<>();
    private final ConcurrentHashMap commentRepository = new ConcurrentHashMap<>();


}
