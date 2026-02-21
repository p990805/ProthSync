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

    /**
     * 해시태그 자동완성 검색
     * 접두사(StartingWith) 기반으로 매칭되는 해시태그를 usageCount 내림차순으로 반환
     */
    public PageResponse<HashtagResponseDTO> searchHashtags(String keyword, Pageable pageable) {
        validateKeyword(keyword);

        String normalizedKeyword = normalizeHashtagKeyword(keyword);
        Page<Hashtag> hashtagPage = hashtagRepository.searchByTagNamePrefix(normalizedKeyword, pageable);

        List<HashtagResponseDTO> hashtags = hashtagPage.getContent().stream()
            .map(HashtagResponseDTO::from)
            .toList();

        return PageResponse.of(hashtags, hashtagPage);
    }

    /**
     * 해시태그 기반 게시물 검색
     * 해시태그 이름 → 해시태그 ID 변환 → 해당 해시태그가 달린 PUBLIC 게시물 조회
     */
    public PageResponse<PostSummaryResponseDTO> searchPostsByHashtag(String hashtagName, Pageable pageable) {
        validateKeyword(hashtagName);

        String normalizedName = normalizeHashtagKeyword(hashtagName);

        Hashtag hashtag = hashtagRepository.findByTagName(normalizedName)
            .orElseThrow(() -> new BusinessException(PostErrorCode.HASHTAG_NOT_FOUND));

        Page<Post> postPage = postRepository.findAllByHashtagIdAndVisibilityPublic(
            hashtag.getHashtagId(), pageable);

        List<PostSummaryResponseDTO> summaries = postPage.getContent().stream()
            .map(post -> PostSummaryResponseDTO.of(
                post, postImageService.getThumbnailUrl(post.getPostId())))
            .toList();

        return PageResponse.of(summaries, postPage);
    }

    /**
     * 사용자 닉네임 검색
     * 포함(Containing) 기반, 대소문자 무시
     */
    public PageResponse<UserSearchResponseDTO> searchUsers(String keyword, Pageable pageable) {
        validateKeyword(keyword);

        Page<User> userPage = userRepository.searchByNickName(keyword.trim(), pageable);

        List<UserSearchResponseDTO> users = userPage.getContent().stream()
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

    /**
     * 해시태그 키워드 정규화
     * '#' 접두사 제거 및 소문자 변환 (Hashtag 엔티티의 정규화 로직과 동일)
     */
    private String normalizeHashtagKeyword(String keyword) {
        return keyword.trim()
            .replaceAll("^#+", "")
            .toLowerCase();
    }
}