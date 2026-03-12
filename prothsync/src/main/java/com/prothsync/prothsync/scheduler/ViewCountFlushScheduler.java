package com.prothsync.prothsync.scheduler;

import com.prothsync.prothsync.service.PostViewCountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountFlushScheduler {

    private final PostViewCountService postViewCountService;

    /**
     * 1분 주기로 Redis에 쌓인 조회수를 DB에 flush
     */
    @Scheduled(fixedRate = 60_000)
    public void flushViewCounts() {
        try {
            postViewCountService.flushViewCountsToDb();
        } catch (Exception e) {
            log.error("조회수 flush 실패", e);
        }
    }
}