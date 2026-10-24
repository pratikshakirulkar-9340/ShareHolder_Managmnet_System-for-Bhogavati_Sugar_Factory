package com.shms.controller;

import com.shms.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Lightweight, unauthenticated endpoint used to verify the application context
 * starts correctly. Superseded by /actuator/health for real health checks.
 */
@RestController
@Tag(name = "Health")
public class HealthController {

    @GetMapping("/api/ping")
    public ApiResponse<Map<String, String>> ping() {
        return ApiResponse.success(Map.of(
                "status", "UP",
                "service", "shms-backend"
        ));
    }
}
