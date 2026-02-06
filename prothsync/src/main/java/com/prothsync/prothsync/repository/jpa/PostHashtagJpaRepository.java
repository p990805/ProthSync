package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.post.PostHashtag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostHashtagJpaRepository extends JpaRepository<PostHashtag, Long> {

    List<PostHashtag> findAllByPostId(Long postId);

    List<PostHashtag> findAllByHashtagId(Long hashtagId);

    void deleteAllByPostId(Long postId);

    boolean existsByPostIdAndHashtagId(Long postId, Long hashtagId);
}
