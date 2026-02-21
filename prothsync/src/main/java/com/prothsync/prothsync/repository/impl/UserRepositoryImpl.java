package com.prothsync.prothsync.repository.impl;

import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.repository.jpa.UserJpaRepository;
import com.prothsync.prothsync.repository.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public boolean existsByUserName(String userName) {
        return userJpaRepository.existsByUserName(userName);
    }

    @Override
    public boolean existsByNickName(String nickName) {
        return userJpaRepository.existsByNickName(nickName);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        return userJpaRepository.findByUserName(userName);
    }

    @Override
    public Optional<User> findByNickName(String nickName) {
        return userJpaRepository.findByNickName(nickName);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public List<User> findNearbyUsers(double lat, double lng, double radiusKm,
        Long excludeUserId, List<String> searchableTypes, int limit) {
        return userJpaRepository.findNearbyUsers(lat, lng, radiusKm, excludeUserId,
            searchableTypes, limit);
    }

    @Override
    public List<User> findNearbyUsersByType(double lat, double lng, double radiusKm,
        Long excludeUserId, String userType, int limit) {
        return userJpaRepository.findNearbyUsersByType(lat, lng, radiusKm, excludeUserId,
            userType, limit);
    }

    @Override
    public Page<User> searchByNickName(String keyword, Pageable pageable) {
        return userJpaRepository.findByNickNameContainingIgnoreCase(keyword, pageable);
    }
}