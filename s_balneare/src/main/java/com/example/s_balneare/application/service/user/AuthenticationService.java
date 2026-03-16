package com.example.s_balneare.application.service.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.moderation.BanRepository;
import com.example.s_balneare.application.port.out.user.AuthenticationUseCase;
import com.example.s_balneare.application.port.out.user.LoginResult;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.user.Customer;
import com.example.s_balneare.domain.user.User;

/**
 * Implementazione dell'interfaccia che permette l'autenticazione dell'utente facendo collaborare l'app Java e il Database.<br>
 * Usa UserRepository per il recupero dati dell’utente;<br>
 * Usa BanRepository per controllo ban dell’utente che cerca di autenticarsi.<br>
 * Viene usata la classe TransactionManager per gestire le SQL Transaction in maniera astratta, indipendente dalla libreria utilizzata.
 *
 * @see AuthenticationUseCase AuthenticationUseCase
 * @see UserRepository UserRepository
 * @see BanRepository BanRepository
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public class AuthenticationService<T extends User> implements AuthenticationUseCase {
    private final UserRepository<T> userRepository;
    private final BanRepository banRepository;
    private final TransactionManager transactionManager;

    public AuthenticationService(UserRepository<T> userRepository,
                                 BanRepository banRepository,
                                 TransactionManager transactionManager) {
        this.userRepository = userRepository;
        this.banRepository = banRepository;
        this.transactionManager = transactionManager;
    }

    /**
     * Autentifica l’utente nell’applicazione.<br>
     * Usa BCrypt per la crittografia della password.
     *
     * @param identifier Username o email dell’utente
     * @param rawPassword Password inserita
     * @return oggetto utile per mantenere la sessione dell’utente all’interno dell’app
     * @throws IllegalArgumentException se username, email e/o password non sono corretti
     * @throws SecurityException se faccio login su un account bannato/disattivato
     * @see LoginResult LoginResult
     * @see BCrypt BCrypt per la crittografia della password
     */
    @Override
    public LoginResult login(String identifier, String rawPassword) {
        return transactionManager.executeInTransaction(context -> {
            //passo 1: verifico username e password
            String hashedPassword = userRepository.findPassword(identifier, context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: invalid username/email and/or password."));
            BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), hashedPassword);
            if (!result.verified) {
                throw new IllegalArgumentException("ERROR: invalid username or password.");
            }

            //passo 2: recupero utente dal DB
            User user = userRepository.findByIdentifier(identifier, context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: user not found."));

            //passo 3: verifico se l'utente ha un ban a livello applicazione
            if (banRepository.isBannedFromApp(user.getId(), context)) {
                throw new SecurityException("ERROR: account banned");
            }

            //passo 4: verifico se l'account è stato disattivato (se Customer)
            if (user instanceof Customer customer && !customer.isActive()) {
                throw new SecurityException("ERROR: account deactivated");
            }

            //passo 5: passo i dati essenziali all'app
            return new LoginResult(user.getId(), user.isOTP(), user.getRole());
        });
    }
}