package com.shms.controller;

import com.shms.dto.ApiResponse;
import com.shms.dto.ShareSugarRequest;
import com.shms.dto.ShareSugarResponse;
import com.shms.service.ShareSugarService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/share-sugar")
@RequiredArgsConstructor
@Tag(name = "Share Sugar (Class A)")
public class ShareSugarController {

    private final ShareSugarService shareSugarService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ShareSugarResponse> record(@Valid @RequestBody ShareSugarRequest request, Authentication auth) {
        return ApiResponse.success("Supply recorded", shareSugarService.recordSupply(request, auth.getName()));
    }

    @GetMapping("/member/{memberId}")
    public ApiResponse<List<ShareSugarResponse>> byMember(@PathVariable String memberId) {
        return ApiResponse.success(shareSugarService.getByMemberId(memberId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<ShareSugarResponse>> byDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.success(shareSugarService.getByDateRange(from, to));
    }
}
