package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.caserequest.CaseProposal;
import com.prothsync.prothsync.entity.caserequest.ProposalStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CaseProposalJpaRepository extends JpaRepository<CaseProposal, Long> {

    Page<CaseProposal> findAllByCaseRequestId(Long caseRequestId, Pageable pageable);

    Page<CaseProposal> findAllByProposerId(Long proposerId, Pageable pageable);

    boolean existsByCaseRequestIdAndProposerId(Long caseRequestId, Long proposerId);

    List<CaseProposal> findAllByCaseRequestIdAndStatus(Long caseRequestId, ProposalStatus status);

    long countByCaseRequestId(Long caseRequestId);

    @Modifying
    @Query("UPDATE CaseProposal cp SET cp.deletedAt = CURRENT_TIMESTAMP, cp.deletedBy = :userId WHERE cp.caseRequestId = :caseRequestId AND cp.deletedAt IS NULL")
    int softDeleteAllByCaseRequestId(@Param("caseRequestId") Long caseRequestId, @Param("userId") Long userId);
}