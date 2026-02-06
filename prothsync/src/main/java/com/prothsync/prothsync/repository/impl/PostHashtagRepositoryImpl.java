package com.prothsync.prothsync.repository.impl;

import com.prothsync.prothsync.entity.post.PostHashtag;
import com.prothsync.prothsync.repository.jpa.PostHashtagJpaRepository;
import com.prothsync.prothsync.repository.repository.PostHashtagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostHashtagRepositoryImpl implements PostHashtagRepository {

    private final PostHashtagJpaRepository postHashtagJpaRepository;

    @Override
    public PostHashtag save(PostHashtag postHashtag) {
        return postHashtagJpaRepository.save(postHashtag);
    }

    @Override
    public List<PostHashtag> saveAll(List<PostHashtag> postHashtags) {
        return postHashtagJpaRepository.saveAll(postHashtags);
    }

    @Override
    public List<PostHashtag> findAllByPostId(Long postId) {
        return postHashtagJpaRepository.findAllByPostId(postId);
    }

    @Override
    public List<PostHashtag> findAllByHashtagId(Long hashtagId) {
        return postHashtagJpaRepository.findAllByHashtagId(hashtagId);
    }

    @Override
    public void delete(PostHashtag postHashtag) {
        postHashtagJpaRepository.delete(postHashtag);
    }

    @Override
    public void deleteAllByPostId(Long postId) {
        postHashtagJpaRepository.deleteAllByPostId(postId);
    }

    @Override
    public boolean existsByPostIdAndHashtagId(Long postId, Long hashtagId) {
        return postHashtagJpaRepository.existsByPostIdAndHashtagId(postId, hashtagId);
    }
}
