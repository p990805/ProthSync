package com.prothsync.prothsync.repository.impl;

import com.prothsync.prothsync.entity.user.Follow;
import com.prothsync.prothsync.repository.jpa.FollowJpaRepository;
import com.prothsync.prothsync.repository.repository.FollowRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepository {

    private final FollowJpaRepository followJpaRepository;

    @Override
    public Follow save(Follow follow) {
        return followJpaRepository.save(follow);
    }

    @Override
    public Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId) {
        return followJpaRepository.findByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Override
    public boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId) {
        return followJpaRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Override
    public Page<Follow> findAllByFollowingId(Long followingId, Pageable pageable) {
        return followJpaRepository.findAllByFollowingId(followingId, pageable);
    }

    @Override
    public Page<Follow> findAllByFollowerId(Long followerId, Pageable pageable) {
        return followJpaRepository.findAllByFollowerId(followerId, pageable);
    }

    @Override
    public void delete(Follow follow) {
        followJpaRepository.delete(follow);
    }

    @Override
    public int countByFollowingId(Long followingId) {
        return followJpaRepository.countByFollowingId(followingId);
    }

    @Override
    public int countByFollowerId(Long followerId) {
        return followJpaRepository.countByFollowerId(followerId);
    }
}
