package com.dev.safwan.repositories;

import com.dev.safwan.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session,Long> {
    Session save(Session session);
}
