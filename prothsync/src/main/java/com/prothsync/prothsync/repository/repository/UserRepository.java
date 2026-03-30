package com.prothsync.prothsync.repository.repository;

import com.prothsync.prothsync.entity.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository {

    User save (User user);
    boolean existsByUserName(String userName);
    boolean existsByNickName(String nickName);
    boolean existsByEmail(String email);
    Optional<User> findById(Long userId);
    Optional<User> findByUserName(String userName);
    Optional<User> findByNickName(String nickName);
    Optional<User> findByEmail(String email);

    List<User> findNearbyUsers(double lat, double lng, double radiusKm,
        Long excludeUserId, List<String> searchableTypes, int limit);
    List<User> findNearbyUsersByType(double lat, double lng, double radiusKm,
        Long excludeUserId, String userType, int limit);

    List<User> findAllByIds(List<Long> userIds);

    Page<User> searchByNickName(String keyword, Pageable pageable);

    Page<User> searchByNickNameExcludingBlockedUsers(
        String keyword, Long currentUserId, Pageable pageable);

    Page<User> findAll(Pageable pageable);

    long count();

    void incrementFollowerCount(Long userId);
    void decrementFollowerCount(Long userId);
    void incrementFollowingCount(Long userId);
    void decrementFollowingCount(Long userId);
}