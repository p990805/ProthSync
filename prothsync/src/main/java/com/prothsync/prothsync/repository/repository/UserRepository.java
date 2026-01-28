package com.prothsync.prothsync.repository.repository;

import com.prothsync.prothsync.entity.user.User;
import java.util.Optional;

public interface UserRepository {

    User save (User user);
    boolean existsByUserName(String userName);
    boolean existsByNickName(String nickName);
    boolean existsByEmail(String email);
    Optional<User> findById(Long userId);
    Optional<User> findByUserName(String userName);
}
