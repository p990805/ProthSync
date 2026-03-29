package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.FollowResponseDTO;
import com.prothsync.prothsync.dto.FollowUserResponseDTO;
import com.prothsync.prothsync.entity.notification.NotificationType;
import com.prothsync.prothsync.entity.notification.ReferenceType;
import com.prothsync.prothsync.entity.user.Follow;
import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.UserErrorCode;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.repository.repository.FollowRepository;
import com.prothsync.prothsync.repository.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    private final NotificationService notificationService;
    private final BlockService blockService;

    @Transactional
    public FollowResponseDTO toggleFollow(Long followerId, Long followingId) {
        User follower = findUserOrThrow(followerId);
        User following = findUserOrThrow(followingId);

        Optional<Follow> existingFollow = followRepository
            .findByFollowerIdAndFollowingId(followerId, followingId);

        if (existingFollow.isPresent()) {
            followRepository.delete(existingFollow.get());
            userRepository.decrementFollowingCount(followerId);
            userRepository.decrementFollowerCount(followingId);
            return FollowResponseDTO.of(followingId, false, following.getFollowerCount() - 1);
        } else {
            blockService.validateNotBlocked(followerId, followingId);
            Follow follow = Follow.create(followerId, followingId);
            followRepository.save(follow);
            userRepository.incrementFollowingCount(followerId);
            userRepository.incrementFollowerCount(followingId);

            notificationService.send(
                NotificationType.FOLLOW, followerId, followingId,
                followerId, ReferenceType.USER);

            return FollowResponseDTO.of(followingId, true, following.getFollowerCount() + 1);
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

        List<Long> followerIds = followPage.getContent().stream()
            .map(Follow::getFollowerId)
            .toList();

        Map<Long, User> userMap = userRepository.findAllByIds(followerIds).stream()
            .collect(Collectors.toMap(User::getUserId, Function.identity()));

        List<FollowUserResponseDTO> followers = followerIds.stream()
            .map(id -> {
                User user = userMap.get(id);
                if (user == null) {
                    throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
                }
                return FollowUserResponseDTO.from(user);
            })
            .toList();

        return PageResponse.of(followers, followPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<FollowUserResponseDTO> getFollowings(Long userId, Pageable pageable) {
        findUserOrThrow(userId);

        Page<Follow> followPage = followRepository.findAllByFollowerId(userId, pageable);

        List<Long> followingIds = followPage.getContent().stream()
            .map(Follow::getFollowingId)
            .toList();

        Map<Long, User> userMap = userRepository.findAllByIds(followingIds).stream()
            .collect(Collectors.toMap(User::getUserId, Function.identity()));

        List<FollowUserResponseDTO> followings = followingIds.stream()
            .map(id -> {
                User user = userMap.get(id);
                if (user == null) {
                    throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
                }
                return FollowUserResponseDTO.from(user);
            })
            .toList();

        return PageResponse.of(followings, followPage);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }
}