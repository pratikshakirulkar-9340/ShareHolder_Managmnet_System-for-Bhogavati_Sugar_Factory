package com.shms.controller;

import com.shms.dto.ApiResponse;
import com.shms.dto.TonnesSugarRequest;
import com.shms.dto.TonnesSugarResponse;
import com.shms.service.TonnesSugarService;
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
@RequestMapping("/api/tonnes-sugar")
@RequiredArgsConstructor
@Tag(name = "Tonnes Sugar (Class C)")
public class TonnesSugarController {

    private final TonnesSugarService tonnesSugarService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TonnesSugarResponse> record(@Valid @RequestBody TonnesSugarRequest request, Authentication auth) {
        return ApiResponse.success("Supply recorded", tonnesSugarService.recordSupply(request, auth.getName()));
    }

    @GetMapping("/member/{memberId}")
    public ApiResponse<List<TonnesSugarResponse>> byMember(@PathVariable String memberId) {
        return ApiResponse.success(tonnesSugarService.getByMemberId(memberId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<TonnesSugarResponse>> byDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.success(tonnesSugarService.getByDateRange(from, to));
    }
}
