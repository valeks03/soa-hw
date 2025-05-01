package varta.comment.repository;

import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class ViewRepository {
    private final ConcurrentMap<String, AtomicInteger> viewRepository = new ConcurrentHashMap<>();

    public int addView(String userId, String postId) {
        AtomicInteger counter = viewRepository.computeIfAbsent(postId, id -> new AtomicInteger(0));
        return counter.incrementAndGet();
    }
}
