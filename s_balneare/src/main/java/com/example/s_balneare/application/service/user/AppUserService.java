package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.out.AppUserRepository;
import com.example.s_balneare.domain.user.AppUser;
//Aggiungere metodi modifica una volta modificate le classi per il pattern DDD light

public abstract class AppUserService {
    protected final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {this.appUserRepository = appUserRepository;}

    public int createUser(AppUser user, String password){return appUserRepository.save(user,password);}
    // create address
    public void deleteUser(int id) {appUserRepository.delete(id);}
    // delete address
    // possibilità di aggiornare address ed eliminaro e aggiungerlo

    public void updateName(AppUser user, String name) {
        //Operazione di aggiornamento del nome
        appUserRepository.update(user, null);
    }
    public void updateSurname(AppUser user, String surname){
        //Operazione di aggiornamento del cognome
        appUserRepository.update(user, null);
    }
    public void updateUsername(AppUser user, String username){
        //Operazione di aggiornamento del username
        appUserRepository.update(user, null);
    }
    public void updateEmail(AppUser user, String email){
        //Operazio di aggiornamento
        appUserRepository.update(user, null);
    }
    public void updatePassword(AppUser user, String password){
        appUserRepository.update(user, password);
    }
}


