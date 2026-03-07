package com.example.s_balneare.application.factory;

import com.example.s_balneare.application.port.in.RegistrationRequest;
import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.CustomerUser;

public class CustomerFactory extends UserFactory {
    @Override
    public CustomerUser createUser(RegistrationRequest request) {
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