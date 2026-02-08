package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.HashtagResponseDTO;
import com.prothsync.prothsync.dto.PostCreateRequestDTO;
import com.prothsync.prothsync.dto.PostImageResponseDTO;
import com.prothsync.prothsync.dto.PostResponseDTO;
import com.prothsync.prothsync.dto.PostSummaryResponseDTO;
import com.prothsync.prothsync.dto.PostUpdateRequestDTO;
import com.prothsync.prothsync.entity.post.Hashtag;
import com.prothsync.prothsync.entity.post.Post;
import com.prothsync.prothsync.entity.post.PostCategory;
import com.prothsync.prothsync.entity.post.PostImage;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.PostErrorCode;
import com.prothsync.prothsync.exception.UserErrorCode;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.repository.repository.PostLikeRepository;
import com.prothsync.prothsync.repository.repository.PostRepository;
import com.prothsync.prothsync.repository.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final HashtagService hashtagService;
    private final PostImageService postImageService;
    private final CommentService commentService;

    @Transactional
    public PostResponseDTO createPost(Long userId, PostCreateRequestDTO request) {
        validateUserExists(userId);

        Post post = Post.create(userId, request.content(), request.category(), request.visibility());
        Post savedPost = postRepository.save(post);

        List<PostImage> savedImages = postImageService.saveImages(
            savedPost.getPostId(), request.imageUrls());
        List<Hashtag> hashtags = hashtagService.connectHashtagsToPost(
            savedPost.getPostId(), request.hashtagNames());

        return toPostResponseDTO(savedPost, savedImages, hashtags, false);
    }

    @Transactional
    public PostResponseDTO getPost(Long postId, Long currentUserId) {
        Post post = findPostOrThrow(postId);
        validatePostAccess(post, currentUserId);

        post.incrementViewCount();
        postRepository.save(post);

        return buildPostResponseDTO(post, currentUserId);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostSummaryResponseDTO> getPublicPosts(Pageable pageable) {
        Page<Post> postPage = postRepository.findAllByVisibilityPublic(pageable);
        return toSummaryPageResponse(postPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostSummaryResponseDTO> getUserPosts(
        Long targetUserId, Long currentUserId, Pageable pageable) {

        validateUserExists(targetUserId);

        Page<Post> postPage;
        if (targetUserId.equals(currentUserId)) {
            postPage = postRepository.findAllByUserId(targetUserId, pageable);
        } else {
            postPage = postRepository.findAllByUserIdAndVisibilityPublic(targetUserId, pageable);
        }

        return toSummaryPageResponse(postPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostSummaryResponseDTO> getPostsByCategory(
        PostCategory category, Pageable pageable) {

        Page<Post> postPage = postRepository.findAllByCategory(category, pageable);
        return toSummaryPageResponse(postPage);
    }

    @Transactional
    public PostResponseDTO updatePost(Long postId, Long userId, PostUpdateRequestDTO request) {
        Post post = findPostOrThrow(postId);
        validatePostOwner(post, userId);

        if (request.content() != null) {
            post.updateContent(request.content());
        }
        if (request.category() != null) {
            post.updateCategory(request.category());
        }
        if (request.visibility() != null) {
            post.updateVisibility(request.visibility());
        }
        postRepository.save(post);

        if (request.imageUrls() != null) {
            postImageService.replaceImages(postId, request.imageUrls());
        }
        if (request.hashtagNames() != null) {
            hashtagService.replaceHashtagsForPost(postId, request.hashtagNames());
        }

        return buildPostResponseDTO(post, userId);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = findPostOrThrow(postId);
        validatePostOwner(post, userId);

        postLikeRepository.deleteAllByPostId(postId);
        commentService.deleteAllByPostId(postId);
        hashtagService.removeAllHashtagsFromPost(postId);
        postImageService.deleteAllByPostId(postId);
        postRepository.delete(post);
    }


    public Post findPostOrThrow(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));
    }

    private void validateUserExists(Long userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    private void validatePostAccess(Post post, Long currentUserId) {
        if (!post.isPublic() && !post.isOwner(currentUserId)) {
            throw new BusinessException(PostErrorCode.POST_ACCESS_DENIED);
        }
    }

    private void validatePostOwner(Post post, Long userId) {
        if (!post.isOwner(userId)) {
            throw new BusinessException(PostErrorCode.POST_ACCESS_DENIED);
        }
    }

    private PostResponseDTO buildPostResponseDTO(Post post, Long currentUserId) {
        List<PostImageResponseDTO> imageDtos = postImageService.getImagesByPostId(post.getPostId());
        List<HashtagResponseDTO> hashtagDtos = hashtagService.getHashtagsByPostId(post.getPostId());
        boolean isLiked = currentUserId != null
            && postLikeRepository.existsByUserIdAndPostId(currentUserId, post.getPostId());

        return PostResponseDTO.of(post, imageDtos, hashtagDtos, isLiked);
    }

    private PostResponseDTO toPostResponseDTO(
        Post post, List<PostImage> images, List<Hashtag> hashtags, boolean isLiked) {

        List<PostImageResponseDTO> imageDtos = images.stream()
            .map(PostImageResponseDTO::from)
            .toList();
        List<HashtagResponseDTO> hashtagDtos = hashtags.stream()
            .map(HashtagResponseDTO::from)
            .toList();

        return PostResponseDTO.of(post, imageDtos, hashtagDtos, isLiked);
    }

    private PageResponse<PostSummaryResponseDTO> toSummaryPageResponse(Page<Post> postPage) {
        List<PostSummaryResponseDTO> summaries = postPage.getContent().stream()
            .map(post -> PostSummaryResponseDTO.of(
                post, postImageService.getThumbnailUrl(post.getPostId())))
            .toList();

        return PageResponse.of(summaries, postPage);
    }
}