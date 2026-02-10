package com.prothsync.prothsync.repository.repository;

import com.prothsync.prothsync.entity.user.Follow;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowRepository {

    Follow save(Follow follow);

    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    Page<Follow> findAllByFollowingId(Long followingId, Pageable pageable);

    Page<Follow> findAllByFollowerId(Long followerId, Pageable pageable);

    void delete(Follow follow);

    int countByFollowingId(Long followingId);

    int countByFollowerId(Long followerId);
}
