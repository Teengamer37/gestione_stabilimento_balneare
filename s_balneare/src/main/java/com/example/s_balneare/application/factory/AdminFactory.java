package com.example.s_balneare.application.factory;

//TODO: fatta

import com.example.s_balneare.application.port.in.RegistrationRequest;
import com.example.s_balneare.domain.user.AdminUser;
import com.example.s_balneare.domain.user.AppUser;

public class AdminFactory extends UserFactory {
        @Override
        public AppUser createUser(RegistrationRequest request) {
            return new AdminUser(
                    request.getId(),
                    request.getEmail(),
                    request.getUsername(),
                    request.getName(),
                    request.getSurname()
            );
        }
}