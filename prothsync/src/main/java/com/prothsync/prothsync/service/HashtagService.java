package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.HashtagResponseDTO;
import com.prothsync.prothsync.entity.post.Hashtag;
import com.prothsync.prothsync.entity.post.PostHashtag;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.PostErrorCode;
import com.prothsync.prothsync.repository.repository.HashtagRepository;
import com.prothsync.prothsync.repository.repository.PostHashtagRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HashtagService {

    private static final int MAX_HASHTAGS_PER_POST = 30;

    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;

    @Transactional
    public List<Hashtag> connectHashtagsToPost(Long postId, List<String> hashtagNames) {
        if (hashtagNames == null || hashtagNames.isEmpty()) {
            return Collections.emptyList();
        }

        validateHashtagCount(hashtagNames);

        List<Hashtag> hashtags = getOrCreateHashtags(hashtagNames);

        for (Hashtag hashtag : hashtags) {
            if (!postHashtagRepository.existsByPostIdAndHashtagId(postId, hashtag.getHashtagId())) {
                PostHashtag postHashtag = PostHashtag.create(postId, hashtag.getHashtagId());
                postHashtagRepository.save(postHashtag);
                hashtag.incrementUsageCount();
                hashtagRepository.save(hashtag);
            }
        }

        return hashtags;
    }

    @Transactional
    public List<Hashtag> replaceHashtagsForPost(Long postId, List<String> newHashtagNames) {
        removeAllHashtagsFromPost(postId);

        if (newHashtagNames == null || newHashtagNames.isEmpty()) {
            return Collections.emptyList();
        }

        return connectHashtagsToPost(postId, newHashtagNames);
    }

    @Transactional
    public void removeAllHashtagsFromPost(Long postId) {
        List<PostHashtag> postHashtags = postHashtagRepository.findAllByPostId(postId);

        for (PostHashtag postHashtag : postHashtags) {
            hashtagRepository.findById(postHashtag.getHashtagId())
                .ifPresent(hashtag -> {
                    hashtag.decrementUsageCount();
                    hashtagRepository.save(hashtag);
                });
        }

        postHashtagRepository.deleteAllByPostId(postId);
    }

    @Transactional(readOnly = true)
    public List<HashtagResponseDTO> getHashtagsByPostId(Long postId) {
        List<PostHashtag> postHashtags = postHashtagRepository.findAllByPostId(postId);

        return postHashtags.stream()
            .map(ph -> hashtagRepository.findById(ph.getHashtagId()))
            .filter(java.util.Optional::isPresent)
            .map(java.util.Optional::get)
            .map(HashtagResponseDTO::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<HashtagResponseDTO> getTrendingHashtags(int limit) {
        return hashtagRepository.findTopByUsageCount(limit).stream()
            .map(HashtagResponseDTO::from)
            .toList();
    }

    private List<Hashtag> getOrCreateHashtags(List<String> hashtagNames) {
        List<String> normalizedNames = hashtagNames.stream()
            .map(name -> name.trim().replaceAll("^#+", "").toLowerCase())
            .distinct()
            .toList();

        List<Hashtag> existingHashtags = hashtagRepository.findAllByTagNameIn(normalizedNames);

        Map<String, Hashtag> existingMap = existingHashtags.stream()
            .collect(Collectors.toMap(Hashtag::getTagName, Function.identity()));

        List<Hashtag> result = new ArrayList<>(existingHashtags);

        for (String name : normalizedNames) {
            if (!existingMap.containsKey(name)) {
                Hashtag newHashtag = Hashtag.create(name);
                Hashtag saved = hashtagRepository.save(newHashtag);
                result.add(saved);
            }
        }

        return result;
    }

    private void validateHashtagCount(List<String> hashtagNames) {
        if (hashtagNames.size() > MAX_HASHTAGS_PER_POST) {
            throw new BusinessException(PostErrorCode.HASHTAG_LIMIT_EXCEEDED);
        }
    }
}