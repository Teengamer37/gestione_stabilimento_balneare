package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.user.Customer;

public class ManageCustomerService extends ManageUserService<Customer> {

    public ManageCustomerService(UserRepository<Customer> userRepository, TransactionManager transactionManager) {
        super(userRepository, transactionManager);
    }

    //cambio numero di telefono transazione unica, gestire lato applicazione eventuali messaggi di conferma
    public void updatePhoneNumber(Integer id, String phoneNumber) {
        transactionManager.executeInTransaction(context -> {
            Customer user = getUserOrThrow(id, context);
            user.changePhoneNumber(phoneNumber);
            userRepository.update(user, context);
        });
    }

    //transazione unica disattivazione utente che può essere effettuato sia dall'utente che dall'admin
    public void setCustomerActive(Integer id, boolean active) {
        transactionManager.executeInTransaction(context -> {
            Customer user = getUserOrThrow(id, context);
            user.setActive(active);
            userRepository.update(user, context);
        });
    }
}