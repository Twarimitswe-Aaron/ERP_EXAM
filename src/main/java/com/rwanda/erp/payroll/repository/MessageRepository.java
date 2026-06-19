package com.rwanda.erp.payroll.repository;

import com.rwanda.erp.payroll.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByEmployeeId(UUID employeeId);
}
