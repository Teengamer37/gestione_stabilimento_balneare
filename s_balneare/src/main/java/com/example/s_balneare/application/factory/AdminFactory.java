package com.example.s_balneare.application.factory;

import com.example.s_balneare.application.port.in.RegistrationRequest;
import com.example.s_balneare.domain.user.AdminUser;

public class AdminFactory extends UserFactory {
        @Override
        public AdminUser createUser(RegistrationRequest request) {
            return new AdminUser(
                    request.getId(),
                    request.getEmail(),
                    request.getUsername(),
                    request.getName(),
                    request.getSurname()
            );
        }
}