package com.dev.safwan.repositories;

import com.dev.safwan.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    User save(User user);
    Optional<User> findByEmail(String email);
}
