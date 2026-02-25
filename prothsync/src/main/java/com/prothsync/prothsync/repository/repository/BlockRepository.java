package com.prothsync.prothsync.repository.repository;

import com.prothsync.prothsync.entity.block.Block;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlockRepository {

    Block save(Block block);

    boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

    Optional<Block> findByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

    Page<Block> findAllByBlockerId(Long blockerId, Pageable pageable);

    List<Long> findBlockedIdsByBlockerId(Long blockerId);

    void delete(Block block);
}