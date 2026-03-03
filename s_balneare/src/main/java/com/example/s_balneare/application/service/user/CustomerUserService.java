package com.example.s_balneare.application.service.user;

import com.example.s_balneare.infrastructure.persistence.jdbc.JdbcCustomerUserRepository;

public class CustomerUserService extends  AppUserService{
    public CustomerUserService(JdbcCustomerUserRepository jdbcCustomerUserRepository) {super(jdbcCustomerUserRepository);}
}
