package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.CaseRequestCreateDTO;
import com.prothsync.prothsync.dto.CaseRequestResponseDTO;
import com.prothsync.prothsync.dto.CaseRequestSummaryDTO;
import com.prothsync.prothsync.dto.CaseRequestUpdateDTO;
import com.prothsync.prothsync.dto.NearbyCaseResponseDTO;
import com.prothsync.prothsync.entity.caserequest.CaseCategory;
import com.prothsync.prothsync.entity.caserequest.CaseRequest;
import com.prothsync.prothsync.entity.caserequest.CaseStatus;
import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.CaseErrorCode;
import com.prothsync.prothsync.exception.UserErrorCode;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.repository.repository.CaseProposalRepository;
import com.prothsync.prothsync.repository.repository.CaseRequestRepository;
import com.prothsync.prothsync.repository.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CaseRequestService {

    private static final double DEFAULT_RADIUS_KM = 10.0;
    private static final double MAX_RADIUS_KM = 50.0;
    private static final double MIN_RADIUS_KM = 1.0;
    private static final int DEFAULT_LIMIT = 20;

    private final CaseRequestRepository caseRequestRepository;
    private final CaseProposalRepository caseProposalRepository;
    private final UserRepository userRepository;

    @Transactional
    public CaseRequestResponseDTO createCaseRequest(Long clientId, CaseRequestCreateDTO request) {
        User client = findUserOrThrow(clientId);

        CaseRequest caseRequest = CaseRequest.create(
            clientId,
            request.title(),
            request.description(),
            request.category(),
            request.preferredDeadline(),
            request.budget(),
            request.toothNumbers(),
            client.getLatitude(),
            client.getLongitude()
        );

        CaseRequest saved = caseRequestRepository.save(caseRequest);
        return CaseRequestResponseDTO.from(saved);
    }

    @Transactional(readOnly = true)
    public CaseRequestResponseDTO getCaseRequest(Long caseRequestId) {
        CaseRequest caseRequest = findCaseRequestOrThrow(caseRequestId);
        return CaseRequestResponseDTO.from(caseRequest);
    }

    @Transactional(readOnly = true)
    public PageResponse<CaseRequestSummaryDTO> getOpenCaseRequests(Pageable pageable) {
        Page<CaseRequest> page = caseRequestRepository.findAllByStatus(CaseStatus.OPEN, pageable);
        return toSummaryPageResponse(page);
    }

    @Transactional(readOnly = true)
    public PageResponse<CaseRequestSummaryDTO> getCaseRequestsByCategory(
        CaseCategory category, Pageable pageable) {
        Page<CaseRequest> page = caseRequestRepository.findAllByStatusAndCategory(
            CaseStatus.OPEN, category, pageable);
        return toSummaryPageResponse(page);
    }

    @Transactional(readOnly = true)
    public PageResponse<CaseRequestSummaryDTO> getMyCaseRequests(Long clientId, Pageable pageable) {
        Page<CaseRequest> page = caseRequestRepository.findAllByClientId(clientId, pageable);
        return toSummaryPageResponse(page);
    }

    @Transactional
    public CaseRequestResponseDTO updateCaseRequest(
        Long caseRequestId, Long userId, CaseRequestUpdateDTO request) {

        CaseRequest caseRequest = findCaseRequestOrThrow(caseRequestId);
        validateOwner(caseRequest, userId);
        validateIsOpen(caseRequest);

        if (request.title() != null) {
            caseRequest.updateTitle(request.title());
        }
        if (request.description() != null) {
            caseRequest.updateDescription(request.description());
        }
        if (request.category() != null) {
            caseRequest.updateCategory(request.category());
        }
        if (request.preferredDeadline() != null) {
            caseRequest.updateDeadline(request.preferredDeadline());
        }
        if (request.budget() != null) {
            caseRequest.updateBudget(request.budget());
        }
        if (request.toothNumbers() != null) {
            caseRequest.updateToothNumbers(request.toothNumbers());
        }

        CaseRequest updated = caseRequestRepository.save(caseRequest);
        return CaseRequestResponseDTO.from(updated);
    }

    @Transactional
    public void cancelCaseRequest(Long caseRequestId, Long userId) {
        CaseRequest caseRequest = findCaseRequestOrThrow(caseRequestId);
        validateOwner(caseRequest, userId);

        caseRequest.cancel();
        caseRequestRepository.save(caseRequest);
    }

    @Transactional
    public void deleteCaseRequest(Long caseRequestId, Long userId) {
        CaseRequest caseRequest = findCaseRequestOrThrow(caseRequestId);
        validateOwner(caseRequest, userId);

        caseProposalRepository.deleteAllByCaseRequestId(caseRequestId);
        caseRequestRepository.delete(caseRequest);
    }

    @Transactional(readOnly = true)
    public List<NearbyCaseResponseDTO> findNearbyCases(
        Long currentUserId, Double radiusKm, Integer limit) {

        User currentUser = findUserOrThrow(currentUserId);
        validateUserHasCoordinates(currentUser);

        double radius = resolveRadius(radiusKm);
        int resultLimit = resolveLimit(limit);

        List<CaseRequest> nearbyCases = caseRequestRepository.findNearbyCases(
            currentUser.getLatitude(),
            currentUser.getLongitude(),
            radius,
            currentUserId,
            resultLimit
        );

        return nearbyCases.stream()
            .map(cr -> NearbyCaseResponseDTO.of(cr,
                calculateDistance(currentUser, cr)))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<NearbyCaseResponseDTO> findNearbyCasesByCategory(
        Long currentUserId, CaseCategory category, Double radiusKm, Integer limit) {

        User currentUser = findUserOrThrow(currentUserId);
        validateUserHasCoordinates(currentUser);

        double radius = resolveRadius(radiusKm);
        int resultLimit = resolveLimit(limit);

        List<CaseRequest> nearbyCases = caseRequestRepository.findNearbyCasesByCategory(
            currentUser.getLatitude(),
            currentUser.getLongitude(),
            radius,
            currentUserId,
            category,
            resultLimit
        );

        return nearbyCases.stream()
            .map(cr -> NearbyCaseResponseDTO.of(cr,
                calculateDistance(currentUser, cr)))
            .toList();
    }


    public CaseRequest findCaseRequestOrThrow(Long caseRequestId) {
        return caseRequestRepository.findById(caseRequestId)
            .orElseThrow(() -> new BusinessException(CaseErrorCode.CASE_NOT_FOUND));
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    private void validateOwner(CaseRequest caseRequest, Long userId) {
        if (!caseRequest.isOwner(userId)) {
            throw new BusinessException(CaseErrorCode.CASE_ACCESS_DENIED);
        }
    }

    private void validateIsOpen(CaseRequest caseRequest) {
        if (!caseRequest.isOpen()) {
            throw new BusinessException(CaseErrorCode.CASE_NOT_OPEN);
        }
    }

    private void validateUserHasCoordinates(User user) {
        if (!user.hasCoordinates()) {
            throw new BusinessException(UserErrorCode.USER_COORDINATES_NOT_SET);
        }
    }

    private double resolveRadius(Double radiusKm) {
        if (radiusKm == null) {
            return DEFAULT_RADIUS_KM;
        }
        if (radiusKm < MIN_RADIUS_KM || radiusKm > MAX_RADIUS_KM) {
            throw new BusinessException(UserErrorCode.INVALID_SEARCH_RADIUS);
        }
        return radiusKm;
    }

    private int resolveLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, 50);
    }

    private PageResponse<CaseRequestSummaryDTO> toSummaryPageResponse(Page<CaseRequest> page) {
        List<CaseRequestSummaryDTO> summaries = page.getContent().stream()
            .map(CaseRequestSummaryDTO::from)
            .toList();
        return PageResponse.of(summaries, page);
    }

    /**
     * Haversine 공식으로 사용자와 의뢰 간 거리(km) 계산
     */
    private double calculateDistance(User user, CaseRequest caseRequest) {
        double earthRadiusKm = 6371.0;

        double latDistance = Math.toRadians(caseRequest.getLatitude() - user.getLatitude());
        double lngDistance = Math.toRadians(caseRequest.getLongitude() - user.getLongitude());

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(user.getLatitude()))
            * Math.cos(Math.toRadians(caseRequest.getLatitude()))
            * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadiusKm * c;
    }
}