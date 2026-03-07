package com.example.s_balneare.application.factory;

import com.example.s_balneare.application.port.in.RegistrationRequest;
import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.OwnerUser;

public class OwnerFactory extends UserFactory {
    @Override
    public OwnerUser createUser(RegistrationRequest request) {
        return new OwnerUser(
                request.getId(),
                request.getEmail(),
                request.getUsername(),
                request.getName(),
                request.getSurname()
        );
    }
}