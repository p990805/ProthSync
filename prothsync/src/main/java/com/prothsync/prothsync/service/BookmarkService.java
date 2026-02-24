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
import java.util.List;
import java.util.Optional;
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

        Optional<Bookmark> existing = bookmarkRepository.findByUserIdAndPostId(userId, postId);

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

        List<PostResponseDTO> posts = bookmarkPage.getContent().stream()
            .map(bookmark -> {
                Post post = postRepository.findById(bookmark.getPostId())
                    .orElse(null);
                if (post == null) {
                    return null;
                }
                return buildPostResponseDTO(post, userId);
            })
            .filter(dto -> dto != null)
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

    private PostResponseDTO buildPostResponseDTO(Post post, Long currentUserId) {
        List<PostImageResponseDTO> imageDtos = postImageService.getImagesByPostId(post.getPostId());
        List<HashtagResponseDTO> hashtagDtos = hashtagService.getHashtagsByPostId(post.getPostId());
        boolean isLiked = postLikeRepository.existsByUserIdAndPostId(currentUserId, post.getPostId());
        boolean isBookmarked = bookmarkRepository.existsByUserIdAndPostId(currentUserId, post.getPostId());

        return PostResponseDTO.of(post, imageDtos, hashtagDtos, isLiked, isBookmarked);
    }
}