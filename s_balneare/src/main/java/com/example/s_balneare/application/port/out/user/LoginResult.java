package com.example.s_balneare.application.port.out.user;

import com.example.s_balneare.domain.user.Role;

public record LoginResult(
        Integer userId,
        boolean requiresPasswordChange,
        Role userRole) {}