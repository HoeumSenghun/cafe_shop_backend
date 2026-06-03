package com.cafe_shop.admin.service;

import com.cafe_shop.admin.dto.AdminDtos.CreateStaffRequest;
import com.cafe_shop.admin.dto.AdminDtos.UserResponse;
import com.cafe_shop.user.model.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {
    Page<UserResponse> listUsers(RoleName role, Pageable pageable);
    UserResponse getById(long id);
    UserResponse createStaff(CreateStaffRequest req);
    UserResponse setEnabled(long id, boolean enabled);
}
