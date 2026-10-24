package com.shms.controller;

import com.shms.dto.ApiResponse;
import com.shms.dto.SugarAllocationRequest;
import com.shms.dto.SugarAllocationResponse;
import com.shms.service.SugarAllocationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sugar-allocations")
@RequiredArgsConstructor
@Tag(name = "Sugar Allocation (Bonus)")
@PreAuthorize("hasRole('ADMIN')")
public class SugarAllocationController {

    private final SugarAllocationService sugarAllocationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SugarAllocationResponse> apply(@Valid @RequestBody SugarAllocationRequest request,
                                                        Authentication auth) {
        return ApiResponse.success("Bonus applied", sugarAllocationService.applyBonus(request, auth.getName()));
    }

    @GetMapping
    public ApiResponse<List<SugarAllocationResponse>> history() {
        return ApiResponse.success(sugarAllocationService.getHistory());
    }
}
