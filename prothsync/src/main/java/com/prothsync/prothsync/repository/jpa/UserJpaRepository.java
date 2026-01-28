package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    boolean existsByUserName(String username);

    boolean existsByNickName(String nickName);

    boolean existsByEmail(String email);

    Optional<User> findByUserName(String userName);


}
