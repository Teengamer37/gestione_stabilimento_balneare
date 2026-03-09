package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.in.RegistrationRequest;
import com.example.s_balneare.application.port.out.AddressRepository;
import com.example.s_balneare.application.port.out.AppUserRepository;
import com.example.s_balneare.application.port.out.CustomerUserRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.CustomerUser;

public class CustomerUserService extends AppUserService<CustomerUser> {

    public CustomerUserService(AppUserRepository<CustomerUser> appUserRepository, AddressRepository addressRepository, TransactionManager transactionManager) {
        super(appUserRepository, addressRepository, transactionManager);
    }

    public void updateTelephoneNumber(Integer id, String phoneNumber) {
        transactionManager.executeInTransaction( context -> {
            CustomerUser appUser = getUserOrThrow(id);
            appUser.changePhoneNumber(phoneNumber);
            appUserRepository.update(appUser, context);
            return appUser;
        });
    }

    public void setCustomerActive(Integer id, Boolean active) {
        transactionManager.executeInTransaction( context -> {
            CustomerUser appUser = getUserOrThrow(id);
            appUser.setActive(active);
            appUserRepository.update(appUser, context);
            return appUser;
        });
    }


    @Override
    protected CustomerUser registerUser(RegistrationRequest request, TransactionContext context) {
        Address address = new Address(0,request.getStreet(),request.getStreetNumber(),request.getCity(),request.getZipCode(),request.getCountry());
        Integer addressId = addressRepository.save(address, context);
        return new CustomerUser(
                0, request.getEmail(), request.getUsername(), request.getName(), request.getSurname(), request.getPhoneNumber(), addressId, request.isActive()
        );
    }
}