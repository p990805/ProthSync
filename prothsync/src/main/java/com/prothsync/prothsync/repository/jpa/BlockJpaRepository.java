package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.block.Block;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlockJpaRepository extends JpaRepository<Block, Long> {

    boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

    Optional<Block> findByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

    Page<Block> findAllByBlockerId(Long blockerId, Pageable pageable);

    @Query("SELECT b.blockedId FROM Block b WHERE b.blockerId = :blockerId")
    List<Long> findBlockedIdsByBlockerId(@Param("blockerId") Long blockerId);
}