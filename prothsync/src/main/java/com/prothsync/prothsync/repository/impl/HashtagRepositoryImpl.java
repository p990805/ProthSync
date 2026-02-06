package com.prothsync.prothsync.repository.impl;

import com.prothsync.prothsync.entity.post.Hashtag;
import com.prothsync.prothsync.repository.jpa.HashtagJpaRepository;
import com.prothsync.prothsync.repository.repository.HashtagRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HashtagRepositoryImpl implements HashtagRepository {

    private final HashtagJpaRepository hashtagJpaRepository;

    @Override
    public Hashtag save(Hashtag hashtag) {
        return hashtagJpaRepository.save(hashtag);
    }

    @Override
    public Optional<Hashtag> findById(Long hashtagId) {
        return hashtagJpaRepository.findById(hashtagId);
    }

    @Override
    public Optional<Hashtag> findByTagName(String tagName) {
        return hashtagJpaRepository.findByTagName(tagName);
    }

    @Override
    public List<Hashtag> findAllByTagNameIn(List<String> tagNames) {
        return hashtagJpaRepository.findAllByTagNameIn(tagNames);
    }

    @Override
    public List<Hashtag> findTopByUsageCount(int limit) {
        return hashtagJpaRepository.findTopByUsageCount(limit);
    }

    @Override
    public boolean existsByTagName(String tagName) {
        return hashtagJpaRepository.existsByTagName(tagName);
    }
}