package com.example.s_balneare.application.port.in.user;

public interface UserRegistrationCase<R extends CreateUserRequest> {
    int register(R request, String rawPassword);
}