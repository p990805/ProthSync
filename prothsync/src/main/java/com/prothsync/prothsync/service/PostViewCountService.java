package com.prothsync.prothsync.service;

import com.prothsync.prothsync.repository.jpa.PostJpaRepository;
import com.prothsync.prothsync.repository.repository.PostRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostViewCountService {

    private static final String VIEW_COUNT_KEY_PREFIX = "post:viewcount:";

    private final RedisTemplate<String, String> redisTemplate;
    private final PostRepository postRepository;

    /**
     * 조회수 증가 — Redis INCR (DB 접근 없음, O(1) atomic)
     */
    public void increaseViewCount(Long postId) {
        String key = VIEW_COUNT_KEY_PREFIX + postId;
        redisTemplate.opsForValue().increment(key);
    }

    /**
     * Redis에 쌓인 delta 값 조회 (DTO 응답 시 DB값과 합산용)
     */
    public int getViewCountDelta(Long postId) {
        String key = VIEW_COUNT_KEY_PREFIX + postId;
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Integer.parseInt(value) : 0;
    }

    /**
     * 스케줄러에서 호출 — Redis에 쌓인 조회수 delta를 DB에 일괄 반영 후 키 삭제
     *
     * getAndDelete()를 사용하여 읽기+삭제를 atomic하게 처리하고,
     * flush 중 유입되는 새 조회수 유실을 방지한다.
     */
    @Transactional
    public void flushViewCountsToDb() {
        Set<String> keys = redisTemplate.keys(VIEW_COUNT_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return;
        }

        int flushedCount = 0;

        for (String key : keys) {
            String value = redisTemplate.opsForValue().getAndDelete(key);
            if (value == null) {
                continue;
            }

            int delta = Integer.parseInt(value);
            if (delta <= 0) {
                continue;
            }

            Long postId = Long.parseLong(key.replace(VIEW_COUNT_KEY_PREFIX, ""));
            postRepository.incrementViewCount(postId, delta);
            flushedCount++;
        }

        if (flushedCount > 0) {
            log.info("조회수 flush 완료 - {}개 게시글 반영", flushedCount);
        }
    }
}