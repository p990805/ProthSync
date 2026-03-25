package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.LikeResponseDTO;
import com.prothsync.prothsync.entity.notification.NotificationType;
import com.prothsync.prothsync.entity.notification.ReferenceType;
import com.prothsync.prothsync.entity.post.Post;
import com.prothsync.prothsync.entity.post.PostLike;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.PostErrorCode;
import com.prothsync.prothsync.repository.repository.PostLikeRepository;
import com.prothsync.prothsync.repository.repository.PostRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;
    private final BlockService blockService;

    @Transactional
    public LikeResponseDTO toggleLike(Long postId, Long userId) {
        Post post = findPostOrThrow(postId);

        Optional<PostLike> existingLike = postLikeRepository.findByUserIdAndPostId(userId, postId);

        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            postRepository.decrementLikeCount(postId);
            return LikeResponseDTO.of(postId, false, post.getLikeCount() - 1);
        } else {
            blockService.validateNotBlocked(userId, post.getUserId());
            PostLike postLike = PostLike.create(userId, postId);
            postLikeRepository.save(postLike);
            postRepository.incrementLikeCount(postId);

            notificationService.send(
                NotificationType.LIKE, userId, post.getUserId(), postId, ReferenceType.POST
            );
            return LikeResponseDTO.of(postId, true, post.getLikeCount() + 1);
        }
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long postId, Long userId) {
        return postLikeRepository.existsByUserIdAndPostId(userId, postId);
    }

    private Post findPostOrThrow(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));
    }
}