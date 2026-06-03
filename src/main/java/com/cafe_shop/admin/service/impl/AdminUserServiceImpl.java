package com.cafe_shop.admin.service.impl;

import com.cafe_shop.admin.dto.AdminDtos.CreateStaffRequest;
import com.cafe_shop.admin.dto.AdminDtos.UserResponse;
import com.cafe_shop.admin.service.AdminUserService;
import com.cafe_shop.common.exception.BusinessException;
import com.cafe_shop.common.exception.ResourceNotFoundException;
import com.cafe_shop.security.SecurityUtils;
import com.cafe_shop.user.model.Role;
import com.cafe_shop.user.model.RoleName;
import com.cafe_shop.user.model.User;
import com.cafe_shop.user.repository.RoleRepository;
import com.cafe_shop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> listUsers(RoleName role, Pageable pageable) {
        Page<User> page = role == null
                ? userRepository.findAll(pageable)
                : userRepository.findByRoleName(role, pageable);
        return page.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(long id) {
        return toResponse(getUser(id));
    }

    @Override
    @Transactional
    public UserResponse createStaff(CreateStaffRequest req) {
        if (req.role() != RoleName.CASHIER && req.role() != RoleName.ADMIN) {
            throw new BusinessException("Staff role must be CASHIER or ADMIN");
        }
        if (userRepository.existsByEmail(req.email().toLowerCase())) {
            throw new BusinessException("Email already registered");
        }

        Role role = roleRepository.findByName(req.role())
                .orElseGet(() -> roleRepository.save(new Role(req.role())));

        User user = new User();
        user.setEmail(req.email().toLowerCase());
        user.setFullName(req.fullName());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setRoles(Set.of(role));

        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse setEnabled(long id, boolean enabled) {
        User user = getUser(id);
        if (user.getId().equals(SecurityUtils.currentUser().userId()) && !enabled) {
            throw new BusinessException("Cannot disable your own account");
        }
        user.setEnabled(enabled);
        return toResponse(userRepository.save(user));
    }

    private User getUser(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserResponse toResponse(User user) {
        Set<RoleName> roles = user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet());
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .enabled(user.isEnabled())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
