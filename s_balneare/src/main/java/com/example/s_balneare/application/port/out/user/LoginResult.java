package com.example.s_balneare.application.port.out.user;

public record LoginResult(
        Integer userId,
        boolean requiresPasswordChange) {}