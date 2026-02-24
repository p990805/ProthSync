package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.BlockResponseDTO;
import com.prothsync.prothsync.dto.BlockedUserResponseDTO;
import com.prothsync.prothsync.entity.block.Block;
import com.prothsync.prothsync.entity.user.Follow;
import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.exception.BlockErrorCode;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.UserErrorCode;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.repository.repository.BlockRepository;
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
public class BlockService {

    private final BlockRepository blockRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;


    @Transactional
    public BlockResponseDTO block(Long blockerId, Long blockedId) {
        findUserOrThrow(blockerId);
        findUserOrThrow(blockedId);

        if (blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
            throw new BusinessException(BlockErrorCode.ALREADY_BLOCKED);
        }

        removeFollowIfExists(blockerId, blockedId);
        removeFollowIfExists(blockedId, blockerId);

        Block block = Block.create(blockerId, blockedId);
        blockRepository.save(block);

        return BlockResponseDTO.of(blockedId, true);
    }

    @Transactional
    public BlockResponseDTO unblock(Long blockerId, Long blockedId) {
        Block block = blockRepository.findByBlockerIdAndBlockedId(blockerId, blockedId)
            .orElseThrow(() -> new BusinessException(BlockErrorCode.BLOCK_NOT_FOUND));

        blockRepository.delete(block);

        return BlockResponseDTO.of(blockedId, false);
    }

    @Transactional(readOnly = true)
    public PageResponse<BlockedUserResponseDTO> getBlockedUsers(Long blockerId, Pageable pageable) {
        findUserOrThrow(blockerId);

        Page<Block> blockPage = blockRepository.findAllByBlockerId(blockerId, pageable);

        List<BlockedUserResponseDTO> blockedUsers = blockPage.getContent().stream()
            .map(block -> {
                User blockedUser = findUserOrThrow(block.getBlockedId());
                return BlockedUserResponseDTO.of(blockedUser, block.getCreatedAt());
            })
            .toList();

        return PageResponse.of(blockedUsers, blockPage);
    }

    @Transactional(readOnly = true)
    public boolean isBlocked(Long blockerId, Long blockedId) {
        return blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId);
    }

    @Transactional(readOnly = true)
    public List<Long> getBlockedIds(Long blockerId) {
        return blockRepository.findBlockedIdsByBlockerId(blockerId);
    }

    @Transactional(readOnly = true)
    public void validateNotBlocked(Long userId, Long targetUserId) {
        if (blockRepository.existsByBlockerIdAndBlockedId(userId, targetUserId)
            || blockRepository.existsByBlockerIdAndBlockedId(targetUserId, userId)) {
            throw new BusinessException(BlockErrorCode.BLOCKED_USER);
        }
    }

    private void removeFollowIfExists(Long followerId, Long followingId) {
        Optional<Follow> follow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId);
        if (follow.isPresent()) {
            followRepository.delete(follow.get());

            User follower = findUserOrThrow(followerId);
            User following = findUserOrThrow(followingId);
            follower.decrementFollowingCount();
            following.decrementFollowerCount();
            userRepository.save(follower);
            userRepository.save(following);
        }
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

}
