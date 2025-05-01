package varta.comment.repository;

import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class LikeRepository {
    private final ConcurrentMap<String, AtomicInteger> likeRepo = new ConcurrentHashMap<>();

    public int addLike(String userId, String postId) {
        AtomicInteger counter = likeRepo.computeIfAbsent(postId, id -> new AtomicInteger(0));
        return counter.incrementAndGet();
    }
}
