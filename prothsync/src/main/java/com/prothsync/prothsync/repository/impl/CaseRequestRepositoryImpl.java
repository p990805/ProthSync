package com.prothsync.prothsync.repository.impl;

import com.prothsync.prothsync.entity.caserequest.CaseCategory;
import com.prothsync.prothsync.entity.caserequest.CaseRequest;
import com.prothsync.prothsync.entity.caserequest.CaseStatus;
import com.prothsync.prothsync.repository.jpa.CaseRequestJpaRepository;
import com.prothsync.prothsync.repository.repository.CaseRequestRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CaseRequestRepositoryImpl implements CaseRequestRepository {

    private final CaseRequestJpaRepository caseRequestJpaRepository;

    @Override
    public CaseRequest save(CaseRequest caseRequest) {
        return caseRequestJpaRepository.save(caseRequest);
    }

    @Override
    public Optional<CaseRequest> findById(Long caseRequestId) {
        return caseRequestJpaRepository.findById(caseRequestId);
    }

    @Override
    public void delete(CaseRequest caseRequest) {
        caseRequestJpaRepository.delete(caseRequest);
    }

    @Override
    public Page<CaseRequest> findAllByStatus(CaseStatus status, Pageable pageable) {
        return caseRequestJpaRepository.findAllByStatus(status, pageable);
    }

    @Override
    public Page<CaseRequest> findAllByClientId(Long clientId, Pageable pageable) {
        return caseRequestJpaRepository.findAllByClientId(clientId, pageable);
    }

    @Override
    public Page<CaseRequest> findAllByCategory(CaseCategory category, Pageable pageable) {
        return caseRequestJpaRepository.findAllByCategory(category, pageable);
    }

    @Override
    public Page<CaseRequest> findAllByStatusAndCategory(CaseStatus status, CaseCategory category, Pageable pageable) {
        return caseRequestJpaRepository.findAllByStatusAndCategory(status, category, pageable);
    }

    @Override
    public List<CaseRequest> findNearbyCases(double lat, double lng, double radiusKm,
        Long excludeUserId, int limit) {
        return caseRequestJpaRepository.findNearbyCases(lat, lng, radiusKm, excludeUserId, limit);
    }

    @Override
    public List<CaseRequest> findNearbyCasesByCategory(double lat, double lng, double radiusKm,
        Long excludeUserId, CaseCategory category, int limit) {
        return caseRequestJpaRepository.findNearbyCasesByCategory(
            lat, lng, radiusKm, excludeUserId, category.name(), limit);
    }
}