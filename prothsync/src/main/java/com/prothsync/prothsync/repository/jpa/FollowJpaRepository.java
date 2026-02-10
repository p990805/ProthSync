package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.user.Follow;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowJpaRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    Page<Follow> findAllByFollowingId(Long followingId, Pageable pageable);

    Page<Follow> findAllByFollowerId(Long followerId, Pageable pageable);

    int countByFollowingId(Long followingId);

    int countByFollowerId(Long followerId);
}
