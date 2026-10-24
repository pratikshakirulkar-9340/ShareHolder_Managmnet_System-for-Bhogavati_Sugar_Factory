package com.shms.controller;

import com.shms.dto.ApiResponse;
import com.shms.dto.TransferDecisionRequest;
import com.shms.dto.TransferRequest;
import com.shms.dto.TransferResponse;
import com.shms.service.ShareTransferService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Tag(name = "Share Transfer")
public class ShareTransferController {

    private final ShareTransferService transferService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TransferResponse> request(@Valid @RequestBody TransferRequest request, Authentication auth) {
        return ApiResponse.success("Transfer requested", transferService.request(request, auth.getName()));
    }

    @PutMapping("/{id}/decision")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TransferResponse> decide(@PathVariable Long id,
                                                 @Valid @RequestBody TransferDecisionRequest decision,
                                                 Authentication auth) {
        return ApiResponse.success("Transfer decision recorded", transferService.decide(id, decision, auth.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<TransferResponse>> all() {
        return ApiResponse.success(transferService.getAll());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<TransferResponse>> pending() {
        return ApiResponse.success(transferService.getPending());
    }
}
