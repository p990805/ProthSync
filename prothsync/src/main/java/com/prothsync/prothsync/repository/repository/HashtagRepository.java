package com.prothsync.prothsync.repository.repository;

import com.prothsync.prothsync.entity.post.Hashtag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HashtagRepository {

    Hashtag save(Hashtag hashtag);

    Optional<Hashtag> findById(Long hashtagId);

    Optional<Hashtag> findByTagName(String tagName);

    List<Hashtag> findAllByTagNameIn(List<String> tagNames);

    List<Hashtag> findAllByIds(List<Long> hashtagIds);

    List<Hashtag> findTopByUsageCount(int limit);

    boolean existsByTagName(String tagName);

    Page<Hashtag> searchByTagNamePrefix(String keyword, Pageable pageable);

}
