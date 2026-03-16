package com.example.s_balneare.application.service.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.s_balneare.application.port.in.user.CreateUserRequest;
import com.example.s_balneare.application.port.in.user.UserRegistrationUseCase;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.User;

/**
 * Implementazione astratta dell'interfaccia che permette la creazione dell'utente facendo collaborare l'app Java e il Database.
 * <p>Usa UserRepository per il salvataggio dell'utente nel DB.
 * <p>Viene usata la classe TransactionManager per gestire le SQL Transaction in maniera astratta, indipendente dalla libreria utilizzata.
 * <p>Estesa nelle classi: CreateCustomerService, CreateOwnerService, CreateAdminService.
 *
 * @see CreateUserRequest CreateUserRequest
 * @see UserRepository UserRepository
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public abstract class CreateUserService<T extends User, R extends CreateUserRequest>
        implements UserRegistrationUseCase<R> {
    private final UserRepository<T> userRepository;
    private final TransactionManager transactionManager;

    public CreateUserService(UserRepository<T> userRepository, TransactionManager transactionManager) {
        this.userRepository = userRepository;
        this.transactionManager = transactionManager;
    }

    /**
     * Metodo astratto che crea e salva nel DB un nuovo utente.
     *
     * @param request Oggetto che contiene tutti i dati necessari per la creazione di un utente
     * @param context Connessione JDBC
     * @return utente appena creato
     */
    protected abstract T registerUser(R request, TransactionContext context);

    /**
     * Metodo che racchiude passi comuni per la creazione di qualsiasi utente.
     * <p>Usa BCrypt per la crittografia della password.
     * <p>Utilizzare questo metodo per la creazione di qualsiasi utente!
     *
     * @param request Oggetto che contiene tutti i dati necessari per la creazione di un utente
     * @param rawPassword Password inserita dall'utente
     * @return ID generato dal DB dell'utente appena creato
     * @see BCrypt BCrypt per la crittografia della password
     */
    @Override
    public final int register(R request, String rawPassword) {
        //passo 1: hash password
        String hashedPassword = BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray());

        //passo 2: apro transazione
        return transactionManager.executeInTransaction(context -> {
            //passo 3: chiamo funzione registerUser
            T userEntity = registerUser(request, context);

            //passo 4: salvo nel DB
            return userRepository.save(userEntity, hashedPassword, context);
        });
    }
}