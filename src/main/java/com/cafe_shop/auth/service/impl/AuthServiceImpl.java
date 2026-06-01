package com.cafe_shop.auth.service.impl;

import com.cafe_shop.auth.dto.AuthDtos.AuthResponse;
import com.cafe_shop.auth.dto.AuthDtos.LoginRequest;
import com.cafe_shop.auth.dto.AuthDtos.RefreshRequest;
import com.cafe_shop.auth.dto.AuthDtos.RegisterRequest;
import com.cafe_shop.auth.model.RefreshToken;
import com.cafe_shop.auth.repository.RefreshTokenRepository;
import com.cafe_shop.auth.service.AuthService;
import com.cafe_shop.common.exception.BusinessException;
import com.cafe_shop.common.exception.UnauthorizedException;
import com.cafe_shop.security.CurrentUser;
import com.cafe_shop.security.JwtProperties;
import com.cafe_shop.security.JwtService;
import com.cafe_shop.user.model.Role;
import com.cafe_shop.user.model.RoleName;
import com.cafe_shop.user.model.User;
import com.cafe_shop.user.repository.RoleRepository;
import com.cafe_shop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email().toLowerCase())) {
            throw new BusinessException("Email already registered");
        }

        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.CUSTOMER)));

        User user = new User();
        user.setEmail(req.email().toLowerCase());
        user.setFullName(req.fullName());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setRoles(Set.of(customerRole));

        User saved = userRepository.save(user);
        return issueTokens(saved);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.email().toLowerCase(), req.password()));

        User user = userRepository.findByEmail(req.email().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        return issueTokens(user);
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshRequest req) {
        RefreshToken rt = refreshTokenRepository.findByToken(req.refreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (rt.isRevoked() || rt.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expired");
        }

        User user = rt.getUser();
        rt.setRevoked(true);
        refreshTokenRepository.save(rt);

        return issueTokens(user);
    }

    @Override
    @Transactional
    public void logout(RefreshRequest req) {
        refreshTokenRepository.findByToken(req.refreshToken())
                .ifPresent(rt -> {
                    rt.setRevoked(true);
                    refreshTokenRepository.save(rt);
                });
    }

    private AuthResponse issueTokens(User user) {
        Set<RoleName> roleNames = user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet());
        CurrentUser cu = new CurrentUser(user.getId(), user.getEmail(), roleNames);

        String accessToken = jwtService.generateAccessToken(cu);
        String refreshTokenValue = UUID.randomUUID().toString() + "." + UUID.randomUUID();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(Instant.now().plusSeconds(jwtProperties.getRefreshTokenTtlDays() * 86400L));
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .expiresInSeconds(jwtProperties.getAccessTokenTtlMinutes() * 60L)
                .build();
    }
}

