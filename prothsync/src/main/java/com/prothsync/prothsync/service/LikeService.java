package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.LikeResponseDTO;
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

    @Transactional
    public LikeResponseDTO toggleLike(Long postId, Long userId) {
        Post post = findPostOrThrow(postId);

        Optional<PostLike> existingLike = postLikeRepository.findByUserIdAndPostId(userId, postId);

        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            post.decrementLikeCount();
            postRepository.save(post);
            return LikeResponseDTO.of(postId, false, post.getLikeCount());
        } else {
            PostLike postLike = PostLike.create(userId, postId);
            postLikeRepository.save(postLike);
            post.incrementLikeCount();
            postRepository.save(post);
            return LikeResponseDTO.of(postId, true, post.getLikeCount());
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