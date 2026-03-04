package com.example.s_balneare.application.factory;

import com.example.s_balneare.application.port.in.RegistrationRequest;
import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.CustomerUser;

public class CustomerFactory extends UserFactory {
    @Override
    public AppUser createUser(RegistrationRequest request) {
        // Costruzione in un colpo solo
        return new CustomerUser(
                request.getId(),
                request.getEmail(),
                request.getUsername(),
                request.getName(),
                request.getSurname(),
                request.getPhoneNumber(),
                request.getAddressId(),
                request.isActive()
        );
    }
}