package com.shms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Email
    private String email;

    /** ADMIN, STAFF, or SHAREHOLDER. Defaults to STAFF if not provided. */
    private String role;

    /** Required only when role = SHAREHOLDER, to link the login to an existing shareholder record. */
    private String memberId;
}
