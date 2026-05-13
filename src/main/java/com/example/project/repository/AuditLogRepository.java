package com.example.project.repository;

import com.example.project.model.AuditLog;
import com.example.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
    List<AuditLog> findByUser(User user);
    List<AuditLog> findByAction(String action);
}