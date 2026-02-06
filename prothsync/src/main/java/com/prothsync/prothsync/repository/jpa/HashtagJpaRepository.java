package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.post.Hashtag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HashtagJpaRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByTagName(String tagName);

    List<Hashtag> findAllByTagNameIn(List<String> tagNames);

    boolean existsByTagName(String tagName);

    @Query("SELECT h FROM Hashtag h ORDER BY h.usageCount DESC LIMIT :limit")
    List<Hashtag> findTopByUsageCount(@Param("limit") int limit);
}
