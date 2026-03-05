package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.out.AppUserRepository;
import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.CustomerUser;

//TODO: gestione metodi per operare su address creare nuova classe

public abstract class AppUserService<T extends AppUser> {
    protected final AppUserRepository<T> appUserRepository;

    public AppUserService(AppUserRepository<T> appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public int createUser(T user, String password){return appUserRepository.save(user,password);}

    public void deleteUser(int id) {
        appUserRepository.delete(id);
    }

    public void updateName(Integer id, String name) {
        T appUser = getUserOrThrow(id);
        appUser.changeName(name);
        appUserRepository.update(appUser);
    }
    public void updateSurname(Integer id, String surname) {
        T appUser = getUserOrThrow(id);
        appUser.changeSurname(surname);
        appUserRepository.update(appUser);
    }
    public void updateUsername(Integer id, String username) {
        T appUser = getUserOrThrow(id);
        appUser.changeUsername(username);
        appUserRepository.update(appUser);
    }
    public void updateEmail(Integer id, String email) {
        T appUser = getUserOrThrow(id);
        appUser.changeEmail(email);
        appUserRepository.update(appUser);
    }
    public void updatePassword(Integer id, String password){
        T appUser = getUserOrThrow(id);
        appUserRepository.updatePassword(appUser, password);
    }

    protected T getUserOrThrow(Integer id){
        return appUserRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("ERROR: User not found with id: " + id));
    }
}