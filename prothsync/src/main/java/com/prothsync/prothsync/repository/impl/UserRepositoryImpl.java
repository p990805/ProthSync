package com.prothsync.prothsync.repository.impl;

import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.repository.jpa.UserJpaRepository;
import com.prothsync.prothsync.repository.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
}
