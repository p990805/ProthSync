package com.prothsync.prothsync.repository.impl;

import com.prothsync.prothsync.entity.caserequest.CaseProposal;
import com.prothsync.prothsync.entity.caserequest.ProposalStatus;
import com.prothsync.prothsync.repository.jpa.CaseProposalJpaRepository;
import com.prothsync.prothsync.repository.repository.CaseProposalRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CaseProposalRepositoryImpl implements CaseProposalRepository {

    private final CaseProposalJpaRepository caseProposalJpaRepository;

    @Override
    public CaseProposal save(CaseProposal proposal) {
        return caseProposalJpaRepository.save(proposal);
    }

    @Override
    public Optional<CaseProposal> findById(Long proposalId) {
        return caseProposalJpaRepository.findById(proposalId);
    }

    @Override
    public void softDelete(CaseProposal proposal, Long userId) {
        proposal.delete(userId);
        caseProposalJpaRepository.save(proposal);
    }

    @Override
    public Page<CaseProposal> findAllByCaseRequestId(Long caseRequestId, Pageable pageable) {
        return caseProposalJpaRepository.findAllByCaseRequestId(caseRequestId, pageable);
    }

    @Override
    public Page<CaseProposal> findAllByProposerId(Long proposerId, Pageable pageable) {
        return caseProposalJpaRepository.findAllByProposerId(proposerId, pageable);
    }

    @Override
    public boolean existsByCaseRequestIdAndProposerId(Long caseRequestId, Long proposerId) {
        return caseProposalJpaRepository.existsByCaseRequestIdAndProposerId(caseRequestId, proposerId);
    }

    @Override
    public List<CaseProposal> findAllByCaseRequestIdAndStatus(Long caseRequestId, ProposalStatus status) {
        return caseProposalJpaRepository.findAllByCaseRequestIdAndStatus(caseRequestId, status);
    }

    @Override
    public long countByCaseRequestId(Long caseRequestId) {
        return caseProposalJpaRepository.countByCaseRequestId(caseRequestId);
    }

    @Override
    public void softDeleteAllByCaseRequestId(Long caseRequestId, Long userId) {
        caseProposalJpaRepository.softDeleteAllByCaseRequestId(caseRequestId, userId);
    }
}