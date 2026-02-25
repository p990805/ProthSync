package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.HashtagResponseDTO;
import com.prothsync.prothsync.dto.PostSummaryResponseDTO;
import com.prothsync.prothsync.dto.UserSearchResponseDTO;
import com.prothsync.prothsync.entity.post.Hashtag;
import com.prothsync.prothsync.entity.post.Post;
import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.PostErrorCode;
import com.prothsync.prothsync.exception.SearchErrorCode;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.repository.repository.BlockRepository;
import com.prothsync.prothsync.repository.repository.HashtagRepository;
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
@Transactional(readOnly = true)
public class SearchService {

    private static final int MAX_KEYWORD_LENGTH = 50;

    private final HashtagRepository hashtagRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostImageService postImageService;
    private final BlockRepository blockRepository;

    public PageResponse<HashtagResponseDTO> searchHashtags(String keyword, Pageable pageable) {
        validateKeyword(keyword);

        String normalizedKeyword = normalizeHashtagKeyword(keyword);
        Page<Hashtag> hashtagPage = hashtagRepository.searchByTagNamePrefix(normalizedKeyword, pageable);

        List<HashtagResponseDTO> hashtags = hashtagPage.getContent().stream()
            .map(HashtagResponseDTO::from)
            .toList();

        return PageResponse.of(hashtags, hashtagPage);
    }

    public PageResponse<PostSummaryResponseDTO> searchPostsByHashtag(
        String hashtagName, Long currentUserId, Pageable pageable) {

        validateKeyword(hashtagName);

        String normalizedName = normalizeHashtagKeyword(hashtagName);

        Hashtag hashtag = hashtagRepository.findByTagName(normalizedName)
            .orElseThrow(() -> new BusinessException(PostErrorCode.HASHTAG_NOT_FOUND));

        Page<Post> postPage = postRepository.findAllByHashtagIdAndVisibilityPublic(
            hashtag.getHashtagId(), pageable);

        List<Long> blockedIds = getBlockedIds(currentUserId);

        List<PostSummaryResponseDTO> summaries = postPage.getContent().stream()
            .filter(post -> !blockedIds.contains(post.getUserId()))
            .map(post -> PostSummaryResponseDTO.of(
                post, postImageService.getThumbnailUrl(post.getPostId())))
            .toList();

        return PageResponse.of(summaries, postPage);
    }



    public PageResponse<UserSearchResponseDTO> searchUsers(
        String keyword, Long currentUserId, Pageable pageable) {

        validateKeyword(keyword);

        Page<User> userPage = userRepository.searchByNickName(keyword.trim(), pageable);

        List<Long> blockedIds = getBlockedIds(currentUserId);

        List<UserSearchResponseDTO> users = userPage.getContent().stream()
            .filter(user -> !blockedIds.contains(user.getUserId()))
            .map(UserSearchResponseDTO::from)
            .toList();

        return PageResponse.of(users, userPage);
    }

    private void validateKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new BusinessException(SearchErrorCode.SEARCH_KEYWORD_EMPTY);
        }
        if (keyword.trim().length() > MAX_KEYWORD_LENGTH) {
            throw new BusinessException(SearchErrorCode.SEARCH_KEYWORD_TOO_LONG);
        }
    }

    private String normalizeHashtagKeyword(String keyword) {
        return keyword.trim()
            .replaceAll("^#+", "")
            .toLowerCase();
    }

    private List<Long> getBlockedIds(Long currentUserId) {
        if (currentUserId == null) {
            return List.of();
        }
        return blockRepository.findBlockedIdsByBlockerId(currentUserId);
    }
}