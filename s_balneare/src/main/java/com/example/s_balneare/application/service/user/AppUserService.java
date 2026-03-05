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
        appUserRepository.update(appUser, null);
    }
    public void updateSurname(Integer id, String surname) {
        AppUser appUser = getUserOrThrow(id);
        appUser.changeSurname(surname);
        appUserRepository.update(appUser, null);
    }
    public void updateUsername(Integer id, String username) {
        AppUser appUser = getUserOrThrow(id);
        appUser.changeUsername(username);
        appUserRepository.update(appUser, null);
    }
    public void updateEmail(Integer id, String email) {
        AppUser appUser = getUserOrThrow(id);
        appUser.changeEmail(email);
        appUserRepository.update(appUser, null);
    }
    public void updatePassword(Integer id, String password){
        AppUser appUser = getUserOrThrow(id);
        //TODO: appUserRepository.updatePassword(appUser, password);
        appUserRepository.update(appUser, password);
    }

    private AppUser getUserOrThrow(Integer id){
        return appUserRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("ERROR: User not found with id: " + id));
    }
}