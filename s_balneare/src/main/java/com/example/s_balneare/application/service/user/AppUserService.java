package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.out.AppUserRepository;
import com.example.s_balneare.domain.user.AppUser;

//TODO: questione esxport moduli warning appUserRepository controlla anche in booking
//TODO: gestione metodi per operare su address creare nuova classe

public abstract class AppUserService {
    protected final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public int createUser(AppUser user, String password){
        return appUserRepository.save(user,password);
    }
    public void deleteUser(int id) {
        appUserRepository.delete(id);
    }

    public void updateName(Integer id, String name) {
        AppUser appUser = getUserOrThrow(id);
        appUser.changeName(name);
        appUserRepository.update(appUser);
    }
    public void updateSurname(Integer id, String surname) {
        AppUser appUser = getUserOrThrow(id);
        appUser.changeSurname(surname);
        appUserRepository.update(appUser);
    }
    public void updateUsername(Integer id, String username) {
        AppUser appUser = getUserOrThrow(id);
        appUser.changeUsername(username);
        appUserRepository.update(appUser);
    }
    public void updateEmail(Integer id, String email) {
        AppUser appUser = getUserOrThrow(id);
        appUser.changeEmail(email);
        appUserRepository.update(appUser);
    }
    public void updatePassword(Integer id, String password){
        AppUser appUser = getUserOrThrow(id);
        appUserRepository.updatePassword(appUser, password);
    }

    protected abstract AppUser getUserOrThrow(Integer id);
}