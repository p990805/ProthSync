package com.prothsync.prothsync.entity.block;

import com.prothsync.prothsync.common.BaseEntity;
import com.prothsync.prothsync.exception.BlockErrorCode;
import com.prothsync.prothsync.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "blocks",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_blocks_blocker_blocked",
            columnNames = {"blocker_id", "blocked_id"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Block extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blockId;

    @Column(name = "blocker_id", nullable = false)
    private Long blockerId;

    @Column(name = "blocked_id", nullable = false)
    private Long blockedId;

    private Block(Long blockerId, Long blockedId) {
        this.blockerId = blockerId;
        this.blockedId = blockedId;
    }

    public static Block create(Long blockerId, Long blockedId) {
        validateBlockerId(blockerId);
        validateBlockedId(blockedId);
        validateNotSelf(blockerId, blockedId);

        return new Block(blockerId, blockedId);
    }

    private static void validateBlockerId(Long blockerId) {
        if (blockerId == null) {
            throw new BusinessException(BlockErrorCode.BLOCKER_ID_NULL);
        }
    }

    private static void validateBlockedId(Long blockedId) {
        if (blockedId == null) {
            throw new BusinessException(BlockErrorCode.BLOCKED_ID_NULL);
        }
    }

    private static void validateNotSelf(Long blockerId, Long blockedId) {
        if (blockerId.equals(blockedId)) {
            throw new BusinessException(BlockErrorCode.CANNOT_BLOCK_SELF);
        }
    }
}