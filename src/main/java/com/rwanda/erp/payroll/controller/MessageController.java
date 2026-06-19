package com.rwanda.erp.payroll.controller;

import com.rwanda.erp.payroll.entity.Message;
import com.rwanda.erp.payroll.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message Management", description = "Endpoints for viewing employee notifications")
public class MessageController {

    private final PayrollService payrollService;

    @GetMapping("/my-messages")
    @Operation(summary = "Get my messages", description = "Returns all database notification messages for the authenticated employee.")
    public ResponseEntity<List<Message>> getMyMessages(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(payrollService.getMyMessages(email));
    }
}
