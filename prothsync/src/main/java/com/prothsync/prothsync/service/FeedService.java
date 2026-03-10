package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.HashtagResponseDTO;
import com.prothsync.prothsync.dto.PostImageResponseDTO;
import com.prothsync.prothsync.dto.PostResponseDTO;
import com.prothsync.prothsync.entity.post.Post;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.repository.repository.BlockRepository;
import com.prothsync.prothsync.repository.repository.BookmarkRepository;
import com.prothsync.prothsync.repository.repository.FollowRepository;
import com.prothsync.prothsync.repository.repository.PostLikeRepository;
import com.prothsync.prothsync.repository.repository.PostRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    private final PostImageService postImageService;
    private final HashtagService hashtagService;
    private final PostLikeRepository postLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BlockRepository blockRepository;

    @Transactional(readOnly = true)
    public PageResponse<PostResponseDTO> getFollowingFeed(Long userId, Pageable pageable) {
        List<Long> followingIds = followRepository.findFollowingIdsByFollowerId(userId);

        if (followingIds.isEmpty()) {
            return PageResponse.empty();
        }

        Set<Long> blockedIdSet = new HashSet<>(blockRepository.findBlockedIdsByBlockerId(userId));
        List<Long> filteredFollowingIds = followingIds.stream()
            .filter(id -> !blockedIdSet.contains(id))
            .toList();

        if (filteredFollowingIds.isEmpty()) {
            return PageResponse.empty();
        }

        Page<Post> postPage = postRepository.findFeedPostsByUserIds(filteredFollowingIds, pageable);

        List<Post> posts = postPage.getContent();
        if (posts.isEmpty()) {
            return PageResponse.of(List.of(), postPage);
        }

        List<Long> postIds = posts.stream()
            .map(Post::getPostId)
            .toList();

        Map<Long, List<PostImageResponseDTO>> imageMap =
            postImageService.getImagesByPostIds(postIds);

        Map<Long, List<HashtagResponseDTO>> hashtagMap =
            hashtagService.getHashtagsByPostIds(postIds);

        Set<Long> likedPostIds = new HashSet<>(
            postLikeRepository.findLikedPostIds(userId, postIds));

        Set<Long> bookmarkedPostIds = new HashSet<>(
            bookmarkRepository.findBookmarkedPostIds(userId, postIds));

        List<PostResponseDTO> feedPosts = posts.stream()
            .map(post -> PostResponseDTO.of(
                post,
                imageMap.getOrDefault(post.getPostId(), List.of()),
                hashtagMap.getOrDefault(post.getPostId(), List.of()),
                likedPostIds.contains(post.getPostId()),
                bookmarkedPostIds.contains(post.getPostId())
            ))
            .toList();

        return PageResponse.of(feedPosts, postPage);
    }
}