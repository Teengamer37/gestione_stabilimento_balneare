package com.example.s_balneare.application.factory;

import com.example.s_balneare.application.port.in.RegistrationRequest;
import com.example.s_balneare.domain.user.Admin;

public class AdminFactory extends UserFactory {
        @Override
        public Admin createUser(RegistrationRequest request) {
            return new Admin(
                    request.getId(),
                    request.getEmail(),
                    request.getUsername(),
                    request.getName(),
                    request.getSurname()
            );
        }
}