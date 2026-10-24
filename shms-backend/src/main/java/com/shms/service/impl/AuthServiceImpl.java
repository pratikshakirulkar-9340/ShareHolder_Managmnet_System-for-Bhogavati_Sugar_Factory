package com.shms.service.impl;

import com.shms.dto.JwtResponse;
import com.shms.dto.LoginRequest;
import com.shms.dto.RegisterRequest;
import com.shms.entity.Role;
import com.shms.entity.Shareholder;
import com.shms.entity.User;
import com.shms.entity.enums.AuditAction;
import com.shms.entity.enums.RoleName;
import com.shms.exception.BadRequestException;
import com.shms.exception.DuplicateResourceException;
import com.shms.exception.ResourceNotFoundException;
import com.shms.exception.UnauthorizedException;
import com.shms.repository.RoleRepository;
import com.shms.repository.ShareholderRepository;
import com.shms.repository.UserRepository;
import com.shms.security.CustomUserDetails;
import com.shms.security.JwtTokenProvider;
import com.shms.service.AuditLogService;
import com.shms.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ShareholderRepository shareholderRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public JwtResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already taken: " + request.getUsername());
        }
        if (StringUtils.hasText(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        RoleName roleName = parseRole(request.getRole());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setEnabled(true);

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Role not seeded: " + roleName));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        if (roleName == RoleName.SHAREHOLDER) {
            if (!StringUtils.hasText(request.getMemberId())) {
                throw new BadRequestException("memberId is required to register a SHAREHOLDER-role account");
            }
            Shareholder shareholder = shareholderRepository.findByMemberId(request.getMemberId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Shareholder", request.getMemberId()));
            user.setShareholder(shareholder);
        }

        User saved = userRepository.save(user);
        auditLogService.log(saved.getUsername(), AuditAction.USER_REGISTERED, "User", saved.getId(),
                "New " + roleName + " account registered");

        return buildJwtResponse(saved, roles);
    }

    @Override
    @Transactional(readOnly = true)
    public JwtResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!user.isEnabled()) {
            throw new UnauthorizedException("This account has been disabled");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        return buildJwtResponse(user, user.getRoles());
    }

    @Override
    @Transactional(readOnly = true)
    public JwtResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User no longer exists"));
        return buildJwtResponse(user, user.getRoles());
    }

    private JwtResponse buildJwtResponse(User user, Set<Role> roles) {
        CustomUserDetails principal = new CustomUserDetails(user);
        String accessToken = jwtTokenProvider.generateAccessToken(principal);
        String refreshToken = jwtTokenProvider.generateRefreshToken(principal);

        List<String> roleNames = roles.stream().map(r -> r.getName().name()).collect(Collectors.toList());

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .roles(roleNames)
                .build();
    }

    private RoleName parseRole(String role) {
        if (!StringUtils.hasText(role)) {
            return RoleName.STAFF;
        }
        try {
            return RoleName.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + role + ". Must be ADMIN, STAFF, or SHAREHOLDER");
        }
    }
}
