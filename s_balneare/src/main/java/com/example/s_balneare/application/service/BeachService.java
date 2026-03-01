package com.example.s_balneare.application.service;

import com.example.s_balneare.application.port.out.BeachRepository;
import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.beach.BeachGeneral;
import com.example.s_balneare.domain.beach.BeachInventory;
import com.example.s_balneare.domain.beach.BeachServices;

import java.util.List;

public class BeachService {
    private final BeachRepository beachRepository;

    public BeachService(BeachRepository beachRepository) {
        this.beachRepository = beachRepository;
    }

    public int createBeach(Beach beach) {
        return beachRepository.save(beach);
    }

    public void updateGeneralInfo(int id, BeachGeneral newGeneral) {
        Beach beach = getBeachOrThrow(id);
        beach.updateGeneralInfo(newGeneral);
        beachRepository.update(beach);
    }

    public void updateInventory(int id, BeachInventory newInventory) {
        Beach beach = getBeachOrThrow(id);
        beach.updateInventory(newInventory);
        beachRepository.update(beach);
    }

    public void updateServices(int id, BeachServices newServices) {
        Beach beach = getBeachOrThrow(id);
        beach.updateServices(newServices);
        beachRepository.update(beach);
    }

    public void updateExtraInfo(int id, String extraInfo) {
        Beach beach = getBeachOrThrow(id);
        beach.editExtraInfo(extraInfo);
        beachRepository.update(beach);
    }

    public void setBeachActive(int id, boolean active) {
        Beach beach = getBeachOrThrow(id);
        beach.setActive(active);
        beachRepository.update(beach);
    }

    public void addSeason(int id, int seasonId) {
        Beach beach = getBeachOrThrow(id);
        beach.addSeason(seasonId);
        beachRepository.update(beach);
    }

    public void removeSeason(int id, int seasonId) {
        Beach beach = getBeachOrThrow(id);
        beach.removeSeason(seasonId);
        beachRepository.update(beach);
    }

    public Beach getBeach(int id) {
        return getBeachOrThrow(id);
    }

    public void deleteBeach(int id) {
        beachRepository.delete(id);
    }

    private Beach getBeachOrThrow(int id) {
        return beachRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Beach not found with id: " + id));
    }
}