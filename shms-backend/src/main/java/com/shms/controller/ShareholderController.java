package com.shms.controller;

import com.shms.dto.ApiResponse;
import com.shms.dto.ShareholderRequest;
import com.shms.dto.ShareholderResponse;
import com.shms.service.ShareholderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shareholders")
@RequiredArgsConstructor
@Tag(name = "Shareholders")
public class ShareholderController {

    private final ShareholderService shareholderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ShareholderResponse> register(@Valid @RequestBody ShareholderRequest request,
                                                       Authentication auth) {
        return ApiResponse.success("Shareholder registered", shareholderService.register(request, auth.getName()));
    }

    @GetMapping("/{memberId}")
    public ApiResponse<ShareholderResponse> getOne(@PathVariable String memberId) {
        return ApiResponse.success(shareholderService.getByMemberId(memberId));
    }

    @PutMapping("/{memberId}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<ShareholderResponse> update(@PathVariable String memberId,
                                                    @Valid @RequestBody ShareholderRequest request,
                                                    Authentication auth) {
        return ApiResponse.success("Shareholder updated", shareholderService.update(memberId, request, auth.getName()));
    }

    @DeleteMapping("/{memberId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable String memberId, Authentication auth) {
        shareholderService.delete(memberId, auth.getName());
        return ApiResponse.message("Shareholder deleted");
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<Page<ShareholderResponse>> search(
            @RequestParam(required = false) String memberId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String village,
            @RequestParam(required = false) String shareClass,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return ApiResponse.success(shareholderService.search(memberId, name, village, shareClass, status, pageable));
    }
}
