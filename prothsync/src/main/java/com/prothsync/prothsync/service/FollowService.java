package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.FollowResponseDTO;
import com.prothsync.prothsync.dto.FollowUserResponseDTO;
import com.prothsync.prothsync.entity.user.Follow;
import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.UserErrorCode;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.repository.repository.FollowRepository;
import com.prothsync.prothsync.repository.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public FollowResponseDTO toggleFollow(Long followerId, Long followingId) {
        User follower = findUserOrThrow(followerId);
        User following = findUserOrThrow(followingId);

        Optional<Follow> existingFollow = followRepository
            .findByFollowerIdAndFollowingId(followerId, followingId);

        if (existingFollow.isPresent()) {
            followRepository.delete(existingFollow.get());
            follower.decrementFollowingCount();
            following.decrementFollowerCount();
            userRepository.save(follower);
            userRepository.save(following);
            return FollowResponseDTO.of(followingId, false, following.getFollowerCount());
        } else {
            Follow follow = Follow.create(followerId, followingId);
            followRepository.save(follow);
            follower.incrementFollowingCount();
            following.incrementFollowerCount();
            userRepository.save(follower);
            userRepository.save(following);
            return FollowResponseDTO.of(followingId, true, following.getFollowerCount());
        }
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Transactional(readOnly = true)
    public PageResponse<FollowUserResponseDTO> getFollowers(Long userId, Pageable pageable) {
        findUserOrThrow(userId);

        Page<Follow> followPage = followRepository.findAllByFollowingId(userId, pageable);

        List<FollowUserResponseDTO> followers = followPage.getContent().stream()
            .map(follow -> findUserOrThrow(follow.getFollowerId()))
            .map(FollowUserResponseDTO::from)
            .toList();

        return PageResponse.of(followers, followPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<FollowUserResponseDTO> getFollowings(Long userId, Pageable pageable) {
        findUserOrThrow(userId);

        Page<Follow> followPage = followRepository.findAllByFollowerId(userId, pageable);

        List<FollowUserResponseDTO> followings = followPage.getContent().stream()
            .map(follow -> findUserOrThrow(follow.getFollowingId()))
            .map(FollowUserResponseDTO::from)
            .toList();

        return PageResponse.of(followings, followPage);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }
}