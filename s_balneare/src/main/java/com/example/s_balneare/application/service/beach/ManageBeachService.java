package com.example.s_balneare.application.service.beach;

import com.example.s_balneare.application.port.in.beach.ManageBeachUseCase;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.booking.BookedInventory;
import com.example.s_balneare.application.port.out.booking.BookedParkingSpaces;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.domain.beach.*;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.layout.Zone;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementazione dell'interfaccia che permette la manipolazione della collezione di Beach tra l'app Java e il Database.
 * @see ManageBeachUseCase ManageBeachUseCase
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public class ManageBeachService implements ManageBeachUseCase {
    private final BeachRepository beachRepository;
    private final BookingRepository bookingRepository;
    private final TransactionManager transactionManager;

    public ManageBeachService(BeachRepository beachRepository, BookingRepository bookingRepository, TransactionManager transactionManager) {
        this.beachRepository = beachRepository;
        this.bookingRepository = bookingRepository;
        this.transactionManager = transactionManager;
    }

    /**
     * Aggiornamento info generali spiaggia:
     * Prendo spiaggia da DB -> modifico parametro -> update nel DB
     * @param id Identificatore spiaggia da cercare nel DB
     * @param newGeneral Nuovo oggetto BeachGeneral da salvare
     */
    @Override
    public void updateGeneralInfo(Integer id, BeachGeneral newGeneral) {
        transactionManager.executeInTransaction(context -> {
            Beach beach = getBeachOrThrow(id, context);
            beach.updateGeneralInfo(newGeneral);
            beachRepository.update(beach, context);
        });
    }

    /**
     * Aggiornamento inventario spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param newInventory Nuovo oggetto BeachInventory da salvare
     */
    @Override
    public void updateInventory(Integer id, BeachInventory newInventory) {
        transactionManager.executeInTransaction(context -> {
            Beach beach = getBeachOrThrow(id, context);

            //ricavo il numero più grande di oggetti extra prenotati in un giorno futuro a quello odierno
            BookedInventory maxInv = bookingRepository.getMaxFutureInventory(id, LocalDate.now(), context);
            //check se le nuove modifiche vanno a soddisfare tutte le richieste delle prenotazioni future
            if (newInventory.countExtraSdraio() < maxInv.sdraio() ||
                    newInventory.countExtraLettini() < maxInv.lettini() ||
                    newInventory.countExtraSedie() < maxInv.sedie() ||
                    newInventory.countCamerini() < maxInv.camerini()) {
                throw new IllegalStateException("ERROR: new inventory does not satisfy all future bookings");
            }

            //se tutto va bene, salvo
            beach.updateInventory(newInventory);
            beachRepository.update(beach, context);
        });
    }

    /**
     * Aggiornamento servizi spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param newServices Nuovo oggetto BeachServices da salvare
     */
    @Override
    public void updateServices(Integer id, BeachServices newServices) {
        transactionManager.executeInTransaction(context -> {
            Beach beach = getBeachOrThrow(id, context);
            beach.updateServices(newServices);
            beachRepository.update(beach, context);
        });
    }

    /**
     * Aggiornamento parcheggio spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param newParking Nuovo oggetto Parking da salvare
     */
    @Override
    public void updateParking(Integer id, Parking newParking) {
        transactionManager.executeInTransaction(context -> {
            Beach beach = getBeachOrThrow(id, context);

            //ricavo il numero più grande di parcheggi prenotati in un giorno futuro a quello odierno
            BookedParkingSpaces maxParked = bookingRepository.getMaxFutureParkings(id, LocalDate.now(), context);
            //check se le nuove modifiche vanno a lasciare o meno posti "vacanti" a certe prenotazioni
            if (newParking.nAutoPark() < maxParked.bookedAuto() ||
                    newParking.nMotoPark() < maxParked.bookedMoto() ||
                    newParking.nBikePark() < maxParked.bookedBike() ||
                    newParking.nElectricPark() < maxParked.bookedElectric()) {
                throw new IllegalStateException("ERROR: new parking capacity leaves some bookings with vacant parking spaces");
            }

            //se tutto va bene, salvo
            beach.updateParking(newParking);
            beachRepository.update(beach, context);
        });
    }

    /**
     * Aggiornamento info extra spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param extraInfo Stringa da sostituire ad extraInfo
     */
    @Override
    public void updateExtraInfo(Integer id, String extraInfo) {
        transactionManager.executeInTransaction(context -> {
            Beach beach = getBeachOrThrow(id, context);
            beach.updateExtraInfo(extraInfo);
            beachRepository.update(beach, context);
        });
    }

    /**
     * Manipolazione stato attività spiaggia: i metodi chiamati applicheranno le Business Rules definite
     * @param id Identificatore spiaggia da cercare nel DB
     * @param active Booleana che specifica il nuovo stato della spiaggia
     */
    @Override
    public void setBeachActive(Integer id, boolean active) {
        transactionManager.executeInTransaction(context -> {
            //se voglio attivare la spiaggia, controllo se possibile
            if (active) {
                Beach beach = getBeachOrThrow(id, context);
                beach.setActive(true);
                beachRepository.updateStatus(id, true, context);
            } else {
                // se devo disattivarla, non vado a controllare altro e uso il metodo veloce per disattivarla
                if (beachRepository.findById(id, context).isPresent()) {
                    beachRepository.updateStatus(id, false, context);
                } else {
                    throw new IllegalArgumentException("ERROR: Beach not found with id " + id);
                }
            }
        });
    }

    /**
     * Aggiunta stagione in una determinata spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param season Nuova stagione da aggiungere
     */
    @Override
    public void addSeason(Integer id, Season season) {
        transactionManager.executeInTransaction(context -> {
            Beach beach = getBeachOrThrow(id, context);
            beach.addSeason(season);
            beachRepository.update(beach, context);
        });
    }

    /**
     * Aggiunta lista di stagioni in una determinata spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param seasons Lista di stagioni da aggiungere (Business Rules sul parametro applicati nei metodi di Beach)
     */
    @Override
    public void addSeasons(Integer id, List<Season> seasons) {
        transactionManager.executeInTransaction(context -> {
            Beach beach = getBeachOrThrow(id, context);
            beach.addSeasons(seasons);
            beachRepository.update(beach, context);
        });
    }

    /**
     * Aggiunta zona in una determinata spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param zone Nuova zona da aggiungere
     */
    @Override
    public void addZone(Integer id, Zone zone) {
        transactionManager.executeInTransaction(context -> {
            Beach beach = getBeachOrThrow(id, context);
            beach.addZone(zone);
            beachRepository.update(beach, context);
        });
    }

    /**
     * Aggiunta lista di zone in una determinata spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param zones Lista di zone da aggiungere (Business Rules sul parametro applicati nei metodi di Beach)
     */
    @Override
    public void addZones(Integer id, List<Zone> zones) {
        transactionManager.executeInTransaction(context -> {
            Beach beach = getBeachOrThrow(id, context);
            beach.addZones(zones);
            beachRepository.update(beach, context);
        });
    }

    //TODO: verificare che non faccia parte di nessuna stagione
    /**
     * Rimozione zona da una determinata spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param zone Zona da rimuovere
     */
    @Override
    public void removeZone(Integer id, Zone zone) {
        transactionManager.executeInTransaction(context -> {
            Beach beach = getBeachOrThrow(id, context);
            beach.removeZone(zone);
            beachRepository.update(beach, context);
        });
    }

    //TODO: verificare che non facciano parte di nessuna stagione
    /**
     * Rimozione lista di zone da una determinata spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param zones Lista di zone da rimuovere (Business Rules sul parametro applicati nei metodi di Beach)
     */
    @Override
    public void removeZones(Integer id, List<Zone> zones) {
        transactionManager.executeInTransaction(context -> {
            Beach beach = getBeachOrThrow(id, context);
            beach.removeZones(zones);
            beachRepository.update(beach, context);
        });
    }

    /**
     * Ricerca spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @return oggetto Beach o eccezione in caso di errore
     * @see #getBeachOrThrow(Integer, TransactionContext) getBeachOrThrow
     */
    @Override
    public Beach getBeach(Integer id) {
        return transactionManager.executeInTransaction(context -> {
            return getBeachOrThrow(id, context);
        });
    }

    /**
     * Ricerca spiaggia tramite ID del proprietario
     * @param ownerId Identificatore proprietario
     * @return oggetto Optional che restituisce Beach se trovato; se non trovato, possono essere usati metodi come
     * Optional.isEmpty() per non rischiare manipolazioni con oggetti null
     */
    @Override
    public Optional<Beach> getOwnerBeach(Integer ownerId) {
        return transactionManager.executeInTransaction(context -> {
            return beachRepository.findByOwnerId(ownerId, context);
        });
    }

    /**
     * Metodo privato che serve nelle operazioni sensibili (in questo caso negli update):
     * cerca in DB -> se non trovo la spiaggia, restituisce NULL -> interrompo tutto
     * @param id Identificativo spiaggia da cercare
     * @return oggetto Beach con quell'ID
     * @throws IllegalArgumentException se la spiaggia non è stata trovata nel DB
     */
    private Beach getBeachOrThrow(Integer id, TransactionContext context) {
        return beachRepository.findById(id, context)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Beach not found with id: " + id));
    }
}