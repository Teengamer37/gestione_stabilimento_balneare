package com.example.s_balneare.application.factory;

import com.example.s_balneare.application.port.in.RegistrationRequest;
import com.example.s_balneare.domain.user.Owner;

public class OwnerFactory extends UserFactory {
    @Override
    public Owner createUser(RegistrationRequest request) {
        return new Owner(
                request.getId(),
                request.getEmail(),
                request.getUsername(),
                request.getName(),
                request.getSurname()
        );
    }
}