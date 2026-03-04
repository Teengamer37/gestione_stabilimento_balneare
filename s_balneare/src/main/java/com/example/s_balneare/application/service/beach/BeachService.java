package com.example.s_balneare.application.service.beach;

import com.example.s_balneare.application.port.out.BeachRepository;
import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.beach.BeachGeneral;
import com.example.s_balneare.domain.beach.BeachInventory;
import com.example.s_balneare.domain.beach.BeachServices;
import com.example.s_balneare.domain.beach.Parking;

import java.util.List;

//contiene metodi per gestire la collezione di beaches salvati nel database
public class BeachService {
    private final BeachRepository beachRepository;

    public BeachService(BeachRepository beachRepository) {
        this.beachRepository = beachRepository;
    }

    //creazione spiaggia
    public int createBeach(Beach beach) {
        return beachRepository.save(beach);
    }

    //aggiornamento info generali spiaggia
    //prendo spiaggia da DB -> modifico parametro -> update nel DB
    public void updateGeneralInfo(Integer id, BeachGeneral newGeneral) {
        Beach beach = getBeachOrThrow(id);
        beach.updateGeneralInfo(newGeneral);
        beachRepository.update(beach);
    }

    //aggiornamento inventario spiaggia
    public void updateInventory(Integer id, BeachInventory newInventory) {
        Beach beach = getBeachOrThrow(id);
        beach.updateInventory(newInventory);
        beachRepository.update(beach);
    }

    //aggiornamento servizi spiaggia
    public void updateServices(Integer id, BeachServices newServices) {
        Beach beach = getBeachOrThrow(id);
        beach.updateServices(newServices);
        beachRepository.update(beach);
    }

    //aggiornamento parcheggio spiaggia
    public void updateParking(Integer id, Parking newParking) {
        Beach beach = getBeachOrThrow(id);
        beach.updateParking(newParking);
        beachRepository.update(beach);
    }

    //aggiornamento info extra spiaggia
    public void updateExtraInfo(Integer id, String extraInfo) {
        Beach beach = getBeachOrThrow(id);
        beach.editExtraInfo(extraInfo);
        beachRepository.update(beach);
    }

    //manipolazione stato attività spiaggia
    public void setBeachActive(Integer id, boolean active) {
        Beach beach = getBeachOrThrow(id);
        beach.setActive(active);
        beachRepository.update(beach);
    }

    //aggiunta stagione in una determinata spiaggia
    public void addSeason(Integer id, Integer seasonId) {
        Beach beach = getBeachOrThrow(id);
        beach.addSeason(seasonId);
        beachRepository.update(beach);
    }

    //aggiunta lista di stagioni in una determinata spiaggia
    public void addSeasons(Integer id, List<Integer> seasonIds) {
        Beach beach = getBeachOrThrow(id);
        beach.addSeasons(seasonIds);
        beachRepository.update(beach);
    }

    //eliminazione stagione da una determinata spiaggia
    public void removeSeason(Integer id, Integer seasonId) {
        Beach beach = getBeachOrThrow(id);
        beach.removeSeason(seasonId);
        beachRepository.update(beach);
    }

    //eliminazione lista di stagioni da una determinata spiaggia
    //-> se SOLO UNA delle stagioni non esiste, l'azione viene revocata
    public void removeSeasons(Integer id, List<Integer> seasonIds) {
        Beach beach = getBeachOrThrow(id);
        beach.removeSeasons(seasonIds);
        beachRepository.update(beach);
    }

    //ricerca spiaggia
    public Beach getBeach(Integer id) {
        return getBeachOrThrow(id);
    }

    //rimozione spiaggia
    public void deleteBeach(Integer id) {
        beachRepository.delete(id);
    }

    //metodo privato che serve nelle operazioni sensibili (in questo caso negli update)
    //cerca in DB -> se restituisce NULL, allora interrompo tutto
    private Beach getBeachOrThrow(Integer id) {
        return beachRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Beach not found with id: " + id));
    }
}