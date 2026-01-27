package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {


}
