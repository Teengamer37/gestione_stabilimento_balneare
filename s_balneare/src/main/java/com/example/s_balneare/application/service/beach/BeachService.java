package com.example.s_balneare.application.service.beach;

import com.example.s_balneare.application.port.in.ManageBeachUseCase;
import com.example.s_balneare.application.port.out.BeachRepository;
import com.example.s_balneare.domain.beach.*;
import com.example.s_balneare.domain.layout.Zone;

import java.util.List;
import java.util.Optional;

//TODO: usare TransactionManager (vedi AppUserService)
/**
 * Implementazione dell'interfaccia che permette la manipolazione della collezione di Beach tra l'app Java e il Database.
 */
public class BeachService implements ManageBeachUseCase {
    private final BeachRepository beachRepository;

    public BeachService(BeachRepository beachRepository) {
        this.beachRepository = beachRepository;
    }

    /**
     * Aggiornamento info generali spiaggia:
     * Prendo spiaggia da DB -> modifico parametro -> update nel DB
     * @param id Identificatore spiaggia da cercare nel DB
     * @param newGeneral Nuovo oggetto BeachGeneral da salvare
     */
    @Override
    public void updateGeneralInfo(Integer id, BeachGeneral newGeneral) {
        Beach beach = getBeachOrThrow(id);
        beach.updateGeneralInfo(newGeneral);
        beachRepository.update(beach);
    }

    /**
     * Aggiornamento inventario spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param newInventory Nuovo oggetto BeachInventory da salvare
     */
    @Override
    public void updateInventory(Integer id, BeachInventory newInventory) {
        Beach beach = getBeachOrThrow(id);
        beach.updateInventory(newInventory);
        beachRepository.update(beach);
    }

    /**
     * Aggiornamento servizi spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param newServices Nuovo oggetto BeachServices da salvare
     */
    @Override
    public void updateServices(Integer id, BeachServices newServices) {
        Beach beach = getBeachOrThrow(id);
        beach.updateServices(newServices);
        beachRepository.update(beach);
    }

    /**
     * Aggiornamento parcheggio spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param newParking Nuovo oggetto Parking da salvare
     */
    @Override
    public void updateParking(Integer id, Parking newParking) {
        Beach beach = getBeachOrThrow(id);
        beach.updateParking(newParking);
        beachRepository.update(beach);
    }

    /**
     * Aggiornamento info extra spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param extraInfo Stringa da sostituire ad extraInfo
     */
    @Override
    public void updateExtraInfo(Integer id, String extraInfo) {
        Beach beach = getBeachOrThrow(id);
        beach.updateExtraInfo(extraInfo);
        beachRepository.update(beach);
    }

    /**
     * Manipolazione stato attività spiaggia: i metodi chiamati applicheranno le Business Rules definite
     * @param id Identificatore spiaggia da cercare nel DB
     * @param active Booleana che specifica il nuovo stato della spiaggia
     */
    @Override
    public void setBeachActive(Integer id, boolean active) {
        //se voglio attivare la spiaggia, controllo se possibile
        if (active) {
            Beach beach = getBeachOrThrow(id);
            beach.setActive(true);
            beachRepository.updateStatus(id, true);
        } else {
            // se devo disattivarla, non vado a controllare altro e uso il metodo veloce per disattivarla
            if (beachRepository.findById(id).isPresent()) {
                beachRepository.updateStatus(id, false);
            } else {
                throw new IllegalArgumentException("ERROR: Beach not found with id " + id);
            }
        }
    }

    /**
     * Aggiunta stagione in una determinata spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param season Nuova stagione da aggiungere
     */
    @Override
    public void addSeason(Integer id, Season season) {
        Beach beach = getBeachOrThrow(id);
        beach.addSeason(season);
        beachRepository.update(beach);
    }

    /**
     * Aggiunta lista di stagioni in una determinata spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param seasons Lista di stagioni da aggiungere (Business Rules sul parametro applicati nei metodi di Beach)
     */
    @Override
    public void addSeasons(Integer id, List<Season> seasons) {
        Beach beach = getBeachOrThrow(id);
        beach.addSeasons(seasons);
        beachRepository.update(beach);
    }

    /**
     * Aggiunta zona in una determinata spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param zone Nuova zona da aggiungere
     */
    @Override
    public void addZone(Integer id, Zone zone) {
        Beach beach = getBeachOrThrow(id);
        beach.addZone(zone);
        beachRepository.update(beach);
    }

    /**
     * Aggiunta lista di zone in una determinata spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param zones Lista di zone da aggiungere (Business Rules sul parametro applicati nei metodi di Beach)
     */
    @Override
    public void addZones(Integer id, List<Zone> zones) {
        Beach beach = getBeachOrThrow(id);
        beach.addZones(zones);
        beachRepository.update(beach);
    }

    /**
     * Rimozione zona da una determinata spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param zone Zona da rimuovere
     */
    @Override
    public void removeZone(Integer id, Zone zone) {
        Beach beach = getBeachOrThrow(id);
        beach.removeZone(zone);
        beachRepository.update(beach);
    }

    /**
     * Rimozione lista di zone da una determinata spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @param zones Lista di zone da rimuovere (Business Rules sul parametro applicati nei metodi di Beach)
     */
    @Override
    public void removeZones(Integer id, List<Zone> zones) {
        Beach beach = getBeachOrThrow(id);
        beach.removeZones(zones);
        beachRepository.update(beach);
    }

    /**
     * Ricerca spiaggia
     * @param id Identificatore spiaggia da cercare nel DB
     * @return oggetto Beach o eccezione in caso di errore
     * @see #getBeachOrThrow(Integer) getBeachOrThrow
     */
    @Override
    public Beach getBeach(Integer id) {
        return getBeachOrThrow(id);
    }

    /**
     * Ricerca spiaggia tramite ID del proprietario
     * @param ownerId Identificatore proprietario
     * @return oggetto Optional che restituisce Beach se trovato; se non trovato, possono essere usati metodi come
     * Optional.isEmpty() per non rischiare manipolazioni con oggetti null
     */
    @Override
    public Optional<Beach> getOwnerBeach(Integer ownerId) {
        return beachRepository.findByOwnerId(ownerId);
    }

    /**
     * Metodo privato che serve nelle operazioni sensibili (in questo caso negli update):
     * cerca in DB -> se non trovo la spiaggia, restituisce NULL -> interrompo tutto
     * @param id Identificativo spiaggia da cercare
     * @return oggetto Beach con quell'ID
     * @throws IllegalArgumentException se la spiaggia non è stata trovata nel DB
     */
    private Beach getBeachOrThrow(Integer id) {
        return beachRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Beach not found with id: " + id));
    }
}