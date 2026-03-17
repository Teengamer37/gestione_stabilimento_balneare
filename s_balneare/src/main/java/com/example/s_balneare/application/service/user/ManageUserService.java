package com.example.s_balneare.application.service.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.Admin;
import com.example.s_balneare.domain.user.Owner;
import com.example.s_balneare.domain.user.User;

/**
 * Implementazione astratta dell'interfaccia che permette la manipolazione dell'utente facendo collaborare l'app Java e il Database.<br>
 * Usa UserRepository per il recupero e la modifica dei dati dell’utente;<br>
 * Viene usata la classe TransactionManager per gestire le SQL Transaction in maniera astratta, indipendente dalla libreria utilizzata.<br>
 * Estesa nelle classi: ManageCustomerService, ManageOwnerService, ManageAdminService.
 *
 * @see UserRepository UserRepositry
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public abstract class ManageUserService<T extends User> {
    protected final UserRepository<T> userRepository;
    protected final TransactionManager transactionManager;

    public ManageUserService(UserRepository<T> userRepository, TransactionManager transactionManager) {
        this.userRepository = userRepository;
        this.transactionManager = transactionManager;
    }

    /**
     * Verifica se la password inserita è uguale a quella salvata nel DB.
     *
     * @param rawPassword    Password non crittografata inserita dall’utente
     * @param hashedPassword Password crittografata ricavata dal DB
     * @throws IllegalArgumentException se la password non corrisponde
     */
    protected void verifyPassword(String rawPassword, String hashedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), hashedPassword);
        //se la password non corrisponde, lancio l'eccezione
        if (!result.verified) {
            throw new IllegalArgumentException("ERROR: wrong Password");
        }
    }

    /**
     * Aggiorna la password di un utente.<br>
     * Disattiva il flag OTP se primo accesso all’account.
     *
     * @param id ID dell‘utente
     * @param oldPassword Vecchia password (per convalida)
     * @param newPassword Nuova password
     * @param OTP Parametro OTP
     * @throws IllegalArgumentException se l’utente non esiste nel DB/la vecchia password non corrisponde
     */
    public void updatePassword(Integer id, String oldPassword, String newPassword, boolean OTP) {
        transactionManager.executeInTransaction(context -> {
            //cerco password vecchia
            String oldHashedPassword = userRepository.findPassword(id, context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: invalid password or user"));

            //verifico password vecchia
            verifyPassword(oldPassword, oldHashedPassword);

            //hash nuova password
            String newHashedPassword = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());

            //aggiorno password nel DB
            userRepository.updatePassword(id, newHashedPassword, context);

            //caso OTP: recupero user, disattivo il flag, aggiorno nel DB
            if (OTP) {
                T user = getUserOrThrow(id, context);
                if (user instanceof Owner owner) {
                    owner.updateOTP(false);
                } else if (user instanceof Admin admin) {
                    admin.updateOTP(false);
                }
                userRepository.update(user, context);
            }
        });
    }

    /**
     * Aggiorna i dati di un utente (nome, cognome, username).<br>
     * DA NON USARE CON CUSTOMER!
     *
     * @param id       ID dell‘utente da aggiornare
     * @param name     Nuovo nome
     * @param surname  Nuovo cognome
     * @param username Nuovo Username
     */
    public void updateDatas(Integer id, String name, String surname, String username) {
        transactionManager.executeInTransaction(context -> {
            T user = getUserOrThrow(id, context);
            user.updateName(name);
            user.updateSurname(surname);
            user.updateUsername(username);
            userRepository.update(user, context);
        });
    }

    /**
     * Aggiorna la email di un utente.
     *
     * @param id              ID dell’utente da aggiornare
     * @param email           Nuova email
     * @param currentPassword Password attuale (per convalida modifiche)
     * @throws IllegalArgumentException se l’utente non esiste nel DB/la vecchia password non corrisponde
     */
    public void updateEmail(Integer id, String email, String currentPassword) {
        transactionManager.executeInTransaction(context -> {
            T user = getUserOrThrow(id, context);

            String currentHashedPassword = userRepository.findPassword(id, context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: password not found"));
            verifyPassword(currentPassword, currentHashedPassword);

            user.updateEmail(email);
            userRepository.update(user, context);
        });
    }

    /**
     * Metodo protetto che preleva dal database uno specifico utente.
     *
     * @param id      ID dell’utente
     * @param context Connessione JDBC
     * @return utente prelevato dal database
     * @throws IllegalArgumentException se l’utente non esiste nel DB
     */
    protected T getUserOrThrow(Integer id, TransactionContext context) {
        return userRepository.findById(id, context)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: user not found with id: " + id));
    }
}