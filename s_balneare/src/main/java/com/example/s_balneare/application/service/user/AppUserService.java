package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.in.RegistrationRequest;
import com.example.s_balneare.application.port.out.AddressRepository;
import com.example.s_balneare.application.port.out.AppUserRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.AppUser;
import at.favre.lib.crypto.bcrypt.BCrypt;

public abstract class AppUserService<T extends AppUser> {
    protected final AppUserRepository<T> appUserRepository;
    protected final AddressRepository addressRepository;
    protected final TransactionManager transactionManager;


    protected abstract T registerUser(RegistrationRequest request, TransactionContext context);

    public AppUserService(AppUserRepository<T> appUserRepository, AddressRepository addressRepository, TransactionManager transactionManager) {
        this.appUserRepository = appUserRepository;
        this.addressRepository = addressRepository;
        this.transactionManager = transactionManager;
    }

    //TODO: Dimmi se pensi che restituire l'id dell'address possa essere utile, per me no,
    // sennò devo utilizzare un record che mi permette più valori di ritorno,
    // il record diventerebbe il tipo di ritnorno di register e create.
    // Nomi classi e organizzazione codice stasera non ho avuto tempo di pensarci dimmi come lo vuoi dividere che opero
    // (NARCIS)
    public int createUser(RegistrationRequest request, String password){
        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        return transactionManager.executeInTransaction( context -> {
            //Creazione utente, il customer in caso si occuperà di chiamare save di address
            T user = registerUser(request, context);
            return appUserRepository.save(user, hashedPassword, context);
            });
    }

    public void updateName(Integer id, String name) {
        transactionManager.executeInTransaction( context -> {
            T appUser = getUserOrThrow(id);
            appUser.updateName(name);
            appUserRepository.update(appUser, context);
        });
    }
    public void updateSurname(Integer id, String surname) {
        transactionManager.executeInTransaction( context -> {
            T appUser = getUserOrThrow(id);
            appUser.updateSurname(surname);
            appUserRepository.update(appUser, context);
        });
    }
    public void updateUsername(Integer id, String username) {
        transactionManager.executeInTransaction( context -> {
            T appUser = getUserOrThrow(id);
            appUser.updateUsername(username);
            appUserRepository.update(appUser, context);
        });
    }
    public void updateEmail(Integer id, String email) {
        transactionManager.executeInTransaction( context -> {
            T appUser = getUserOrThrow(id);
            appUser.updateEmail(email);
            appUserRepository.update(appUser, context);
        });
    }

    public void updatePassword(Integer id, String password){
        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        transactionManager.executeInTransaction( context -> {
            T appUser = getUserOrThrow(id);
            appUserRepository.updatePassword(appUser, hashedPassword, context);
        });
    }

    //TODO:controlla passaggio context in find ERRORE: creazione due connessioni (SOLDI)
    protected T getUserOrThrow(Integer id){
        return appUserRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("ERROR: User not found with id: " + id));
    }
}