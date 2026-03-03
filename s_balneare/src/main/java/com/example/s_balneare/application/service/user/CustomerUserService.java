package com.example.s_balneare.application.service.user;

import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.user.CustomerUser;
import com.example.s_balneare.infrastructure.persistence.jdbc.JdbcCustomerUserRepository;

public class CustomerUserService extends  AppUserService{
    public CustomerUserService(JdbcCustomerUserRepository jdbcCustomerUserRepository) {super(jdbcCustomerUserRepository);}

    public void updateTelephoneNumber(CustomerUser user, String phoneNumber){
        //
        appUserRepository.update(user, null);
    }
    public void updateAddress(CustomerUser user, Address address){
        //chiamo update su address
    }
}
