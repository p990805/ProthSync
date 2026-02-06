package com.prothsync.prothsync.repository.repository;

import com.prothsync.prothsync.entity.post.PostHashtag;
import java.util.List;

public interface PostHashtagRepository {

    PostHashtag save(PostHashtag postHashtag);

    List<PostHashtag> saveAll(List<PostHashtag> postHashtags);

    List<PostHashtag> findAllByPostId(Long postId);

    List<PostHashtag> findAllByHashtagId(Long hashtagId);

    void delete(PostHashtag postHashtag);

    void deleteAllByPostId(Long postId);

    boolean existsByPostIdAndHashtagId(Long postId, Long hashtagId);
}
