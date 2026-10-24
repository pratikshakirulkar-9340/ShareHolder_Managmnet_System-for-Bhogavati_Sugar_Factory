package com.shms.controller;

import com.shms.dto.ApiResponse;
import com.shms.dto.RateRequest;
import com.shms.dto.RateResponse;
import com.shms.service.RateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rates")
@RequiredArgsConstructor
@Tag(name = "Rates")
public class RateController {

    private final RateService rateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RateResponse> create(@Valid @RequestBody RateRequest request, Authentication auth) {
        return ApiResponse.success("Rate created", rateService.create(request, auth.getName()));
    }

    @GetMapping("/current")
    public ApiResponse<RateResponse> current() {
        return ApiResponse.success(rateService.getCurrent());
    }

    @GetMapping
    public ApiResponse<List<RateResponse>> history() {
        return ApiResponse.success(rateService.getHistory());
    }
}
