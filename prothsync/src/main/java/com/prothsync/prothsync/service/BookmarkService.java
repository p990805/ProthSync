package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.BookmarkResponseDTO;
import com.prothsync.prothsync.dto.HashtagResponseDTO;
import com.prothsync.prothsync.dto.PostImageResponseDTO;
import com.prothsync.prothsync.dto.PostResponseDTO;
import com.prothsync.prothsync.entity.post.Bookmark;
import com.prothsync.prothsync.entity.post.Post;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.PostErrorCode;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.repository.repository.BookmarkRepository;
import com.prothsync.prothsync.repository.repository.PostLikeRepository;
import com.prothsync.prothsync.repository.repository.PostRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostImageService postImageService;
    private final HashtagService hashtagService;

    @Transactional
    public BookmarkResponseDTO toggleBookmark(Long postId, Long userId) {
        findPostOrThrow(postId);

        java.util.Optional<Bookmark> existing = bookmarkRepository.findByUserIdAndPostId(userId, postId);

        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            return BookmarkResponseDTO.of(postId, false);
        } else {
            Bookmark bookmark = Bookmark.create(userId, postId);
            bookmarkRepository.save(bookmark);
            return BookmarkResponseDTO.of(postId, true);
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<PostResponseDTO> getMyBookmarks(Long userId, Pageable pageable) {
        Page<Bookmark> bookmarkPage = bookmarkRepository.findAllByUserId(userId, pageable);

        List<Long> postIds = bookmarkPage.getContent().stream()
            .map(Bookmark::getPostId)
            .toList();

        if (postIds.isEmpty()) {
            return PageResponse.of(List.of(), bookmarkPage);
        }

        // ★ 배치 조회 (총 5 쿼리)
        Map<Long, Post> postMap = postRepository.findAllByIds(postIds).stream()
            .collect(Collectors.toMap(Post::getPostId, Function.identity()));

        Map<Long, List<PostImageResponseDTO>> imageMap =
            postImageService.getImagesByPostIds(postIds);

        Map<Long, List<HashtagResponseDTO>> hashtagMap =
            hashtagService.getHashtagsByPostIds(postIds);

        Set<Long> likedPostIds = new HashSet<>(
            postLikeRepository.findLikedPostIds(userId, postIds));

        // ★ DTO 조립 (isBookmarked = 항상 true)
        List<PostResponseDTO> posts = postIds.stream()
            .map(postMap::get)
            .filter(Objects::nonNull)
            .map(post -> PostResponseDTO.of(
                post,
                imageMap.getOrDefault(post.getPostId(), List.of()),
                hashtagMap.getOrDefault(post.getPostId(), List.of()),
                likedPostIds.contains(post.getPostId()),
                true
            ))
            .toList();

        return PageResponse.of(posts, bookmarkPage);
    }

    @Transactional(readOnly = true)
    public boolean isBookmarked(Long postId, Long userId) {
        return bookmarkRepository.existsByUserIdAndPostId(userId, postId);
    }

    private Post findPostOrThrow(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));
    }
}