package com.revature.userservice.repository;

import com.revature.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByMobile(String mobile);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmail(String email);

    boolean existsByMobile(String mobile);

    boolean existsByEmail(String email);
}
