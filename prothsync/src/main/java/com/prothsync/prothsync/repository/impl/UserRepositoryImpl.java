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


}
