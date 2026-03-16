package com.example.s_balneare.application.service.beach;

import com.example.s_balneare.application.port.in.beach.CreateBeachCommand;
import com.example.s_balneare.application.port.in.beach.CreateBeachUseCase;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.common.AddressRepository;
import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.common.Address;

/**
 * Implementazione dello Use Case di aggiunta spiaggia nel DB:
 * <p>Interagisce con AddressRepository per salvare l'indirizzo della nuova spiaggia;
 * <p>Successivamente con BeachRepository per salvare la nuova spiaggia con riferimento alla nuova Address.
 * <p>Viene usata la classe TransactionManager per gestire le SQL Transaction in maniera astratta, indipendente dalla libreria utilizzata.
 *
 * @see CreateBeachUseCase CreateBeachUseCase
 * @see TransactionManager TransactionManager
 * @see AddressRepository AddressRepository
 * @see BeachRepository BeachRepository
 */
public class CreateBeachService implements CreateBeachUseCase {

    private final AddressRepository addressRepository;
    private final BeachRepository beachRepository;
    private final TransactionManager transactionManager;

    public CreateBeachService(AddressRepository addressRepository, BeachRepository beachRepository, TransactionManager transactionManager) {
        this.addressRepository = addressRepository;
        this.beachRepository = beachRepository;
        this.transactionManager = transactionManager;
    }

    /**
     * Crea una nuova spiaggia, aggiornando tutte le tabelle necessarie
     *
     * @param command Oggetto contenente tutti i dettagli di una spiaggia
     * @return ID della spiaggia appena salvata (assegnato dal DB)
     * @see CreateBeachCommand CreateBeachCommand
     */
    @Override
    public Integer createBeach(CreateBeachCommand command) {
        //transactionManager pensa in automatico all'apertura transazione, commit e rollback
        return transactionManager.executeInTransaction(context -> {
            //passo 1: controllo se l'Owner non ha una Beach associata
            if (beachRepository.findByOwnerId(command.ownerId(), context).isPresent()) {
                throw new IllegalStateException("ERROR: owner already has a beach");
            }

            //passo 2: creazione indirizzo e salvataggio nel DB
            Address address = new Address(
                    0,
                    command.street(),
                    command.streetNumber(),
                    command.city(),
                    command.zipCode(),
                    command.country()
            );
            //addressId serve per recuperare l'ID generato dal DB
            Integer addressId = addressRepository.save(address, context);

            //passo 3: creazione spiaggia e salvataggio nel DB
            //salvo lo stato di active, passato da command
            boolean finalActiveState = command.active();
            Beach beach = new Beach(0,
                    command.ownerId(),
                    addressId,
                    command.beachGeneral(),
                    command.beachInventory(),
                    command.beachServices(),
                    command.parking(),
                    command.extraInfo(),
                    command.seasons(),
                    command.zones(),
                    false,
                    false
            );
            //verifico activeState: se è messo a true, ma la spiaggia non è configurata completamente, metto active a false,
            //senza lanciare errori/eccezioni
            //se activeState è false, allora procedo normalmente
            if (finalActiveState) {
                // UC-10 Step 10a: Save as Draft
                beach.setActive(beach.isFullyConfigured());
            }

            //passo 4: fine transaction e ritorno il nuovo ID della spiaggia
            return beachRepository.save(beach, context);
        });
    }
}