package com.shms.controller;

import com.shms.dto.AllotmentRequest;
import com.shms.dto.AllotmentResponse;
import com.shms.dto.ApiResponse;
import com.shms.service.ShareAllotmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/allotments")
@RequiredArgsConstructor
@Tag(name = "Share Allotment")
public class ShareAllotmentController {

    private final ShareAllotmentService allotmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AllotmentResponse> allot(@Valid @RequestBody AllotmentRequest request, Authentication auth) {
        return ApiResponse.success("Shares allotted", allotmentService.allot(request, auth.getName()));
    }

    @GetMapping("/member/{memberId}")
    public ApiResponse<List<AllotmentResponse>> byMember(@PathVariable String memberId) {
        return ApiResponse.success(allotmentService.getByMemberId(memberId));
    }
}
