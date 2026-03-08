package com.example.s_balneare.application.service.beach;

import com.example.s_balneare.application.port.in.ManageBeachUseCase;
import com.example.s_balneare.application.port.out.BeachRepository;
import com.example.s_balneare.domain.beach.*;
import com.example.s_balneare.domain.layout.Zone;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

//contiene metodi per gestire la collezione di beaches salvati nel database
public class BeachService implements ManageBeachUseCase {
    private final BeachRepository beachRepository;

    public BeachService(BeachRepository beachRepository) {
        this.beachRepository = beachRepository;
    }

    //aggiornamento info generali spiaggia
    //prendo spiaggia da DB -> modifico parametro -> update nel DB
    @Override
    public void updateGeneralInfo(Integer id, BeachGeneral newGeneral) {
        Beach beach = getBeachOrThrow(id);
        beach.updateGeneralInfo(newGeneral);
        beachRepository.update(beach);
    }

    //aggiornamento inventario spiaggia
    @Override
    public void updateInventory(Integer id, BeachInventory newInventory) {
        Beach beach = getBeachOrThrow(id);
        beach.updateInventory(newInventory);
        beachRepository.update(beach);
    }

    //aggiornamento servizi spiaggia
    @Override
    public void updateServices(Integer id, BeachServices newServices) {
        Beach beach = getBeachOrThrow(id);
        beach.updateServices(newServices);
        beachRepository.update(beach);
    }

    //aggiornamento parcheggio spiaggia
    @Override
    public void updateParking(Integer id, Parking newParking) {
        Beach beach = getBeachOrThrow(id);
        beach.updateParking(newParking);
        beachRepository.update(beach);
    }

    //aggiornamento info extra spiaggia
    @Override
    public void updateExtraInfo(Integer id, String extraInfo) {
        Beach beach = getBeachOrThrow(id);
        beach.updateExtraInfo(extraInfo);
        beachRepository.update(beach);
    }

    //manipolazione stato attività spiaggia
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

    //aggiunta stagione in una determinata spiaggia
    @Override
    public void addSeason(Integer id, Season season) {
        Beach beach = getBeachOrThrow(id);
        beach.addSeason(season);
        beachRepository.update(beach);
    }

    //aggiunta lista di stagioni in una determinata spiaggia
    @Override
    public void addSeasons(Integer id, List<Season> seasons) {
        Beach beach = getBeachOrThrow(id);
        beach.addSeasons(seasons);
        beachRepository.update(beach);
    }

    //aggiunta zona in una determinata spiaggia
    @Override
    public void addZone(Integer id, Zone zone) {
        Beach beach = getBeachOrThrow(id);
        beach.addZone(zone);
        beachRepository.update(beach);
    }

    //aggiunta lista di zone in una determinata spiaggia
    @Override
    public void addZones(Integer id, List<Zone> zones) {
        Beach beach = getBeachOrThrow(id);
        beach.addZones(zones);
        beachRepository.update(beach);
    }

    @Override
    public void removeZone(Integer id, Zone zone) {
        Beach beach = getBeachOrThrow(id);
        beach.removeZone(zone);
        beachRepository.update(beach);
    }

    @Override
    public void removeZones(Integer id, List<Zone> zones) {
        Beach beach = getBeachOrThrow(id);
        beach.removeZones(zones);
        beachRepository.update(beach);
    }

    //ricerca spiaggia
    @Override
    public Beach getBeach(Integer id) {
        return getBeachOrThrow(id);
    }

    @Override
    public Optional<Beach> getOwnerBeach(Integer ownerId) {
        return beachRepository.findByOwnerId(ownerId);
    }

    //metodo privato che serve nelle operazioni sensibili (in questo caso negli update)
    //cerca in DB -> se restituisce NULL, allora interrompo tutto
    private Beach getBeachOrThrow(Integer id) {
        return beachRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Beach not found with id: " + id));
    }
}