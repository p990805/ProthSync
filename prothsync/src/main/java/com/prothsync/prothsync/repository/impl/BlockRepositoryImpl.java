package com.prothsync.prothsync.repository.impl;

import com.prothsync.prothsync.entity.block.Block;
import com.prothsync.prothsync.repository.jpa.BlockJpaRepository;
import com.prothsync.prothsync.repository.repository.BlockRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BlockRepositoryImpl implements BlockRepository {

    private final BlockJpaRepository blockJpaRepository;

    @Override
    public Block save(Block block) {
        return blockJpaRepository.save(block);
    }

    @Override
    public boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId) {
        return blockJpaRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId);
    }

    @Override
    public Optional<Block> findByBlockerIdAndBlockedId(Long blockerId, Long blockedId) {
        return blockJpaRepository.findByBlockerIdAndBlockedId(blockerId, blockedId);
    }

    @Override
    public Page<Block> findAllByBlockerId(Long blockerId, Pageable pageable) {
        return blockJpaRepository.findAllByBlockerId(blockerId, pageable);
    }

    @Override
    public List<Long> findBlockedIdsByBlockerId(Long blockerId) {
        return blockJpaRepository.findBlockedIdsByBlockerId(blockerId);
    }

    @Override
    public void delete(Block block) {
        blockJpaRepository.delete(block);
    }
}