package com.prothsync.prothsync.repository.repository;

import com.prothsync.prothsync.entity.user.User;

public interface UserRepository {

    User save (User user);
    boolean existsByUserName(String userName);
    boolean existsByNickName(String nickName);
    boolean existsByEmail(String email);
}
