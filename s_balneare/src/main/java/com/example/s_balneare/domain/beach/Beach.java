package com.example.s_balneare.domain.beach;

import java.util.ArrayList;
import java.util.List;

public class Beach {
    //attributi
    private final Integer id;
    private Integer ownerId;
    private final Integer addressId;

    private BeachGeneral beachGeneral;
    private BeachInventory beachInventory;
    private BeachServices beachServices;
    private Parking parking;
    private final List<Integer> seasonIds;

    private String extraInfo;
    private boolean active;


    //costruttore
    public Beach(Integer id, Integer ownerId, Integer addressId, BeachGeneral beachGeneral, BeachInventory beachInventory, BeachServices beachServices, Parking parking, String extraInfo, List<Integer> seasonIds, boolean active) {
        this.id = id;
        updateOwnerId(ownerId);
        if (addressId == null) throw new IllegalArgumentException("ERROR: addressId cannot be null");
        this.addressId = addressId;
        this.beachGeneral = beachGeneral;
        this.beachInventory = beachInventory;
        this.beachServices = beachServices;
        this.parking = parking;
        updateExtraInfo(extraInfo);
        if (seasonIds == null || seasonIds.isEmpty()) this.seasonIds = new ArrayList<Integer>();
        else this.seasonIds = new ArrayList<>(seasonIds);
        setActive(active);
    }


    //getters
    public Integer getId() {
        return id;
    }
    public Integer getOwnerId() {
        return ownerId;
    }
    public Integer getAddressId() {
        return addressId;
    }
    public BeachGeneral getBeachGeneral() {
        return beachGeneral;
    }
    public BeachInventory getBeachInventory() {
        return beachInventory;
    }
    public BeachServices getBeachServices() {
        return beachServices;
    }
    public Parking getParking() {
        return parking;
    }
    public String getExtraInfo() {
        return extraInfo;
    }
    public List<Integer> getSeasonIds() {
        return seasonIds;
    }
    public boolean isActive() {
        return active;
    }


    //---- METODI DI BUSINESS ----
    //permette di modificare la sezione di info extra
    public void updateExtraInfo(String info) {
        if (info == null) extraInfo = "";
        else {
            checkExtraInfo(info);
            extraInfo = info;
        }
    }

    //permette di aggiungere/rimuovere un owner alla/dalla spiaggia
    public void updateOwnerId(Integer ownerId) {
        checkOwnerId(ownerId);
        this.ownerId = ownerId;
    }

    //aggiunge una stagione
    public void addSeason(Integer seasonId) {
        checkSeasonId(seasonId);
        seasonIds.add(seasonId);
    }

    //aggiunge una lista di stagioni
    public void addSeasons(List<Integer> seasonIds) {
        checkSeasonIds(seasonIds);
        this.seasonIds.addAll(seasonIds);
    }

    //rimuove una stagione
    public void removeSeason (Integer seasonId) {
        checkSeasonId(seasonId);
        //tentativo eliminazione
        if (seasonIds.contains(seasonId)) seasonIds.remove(seasonId);
        else throw new IllegalArgumentException("ERROR: seasonId not found in seasons");
    }

    //rimuove una lista di stagioni
    public void removeSeasons (List<Integer> seasonIds) {
        checkSeasonIds(seasonIds);
        //mi assicuro che TUTTE le stagioni nella lista siano presenti nella spiaggia
        for (Integer seasonId : seasonIds) {
            if (!this.seasonIds.contains(seasonId)) throw new IllegalArgumentException("ERROR: at least one seasonId in the list is not found in seasons");
        }
        this.seasonIds.removeAll(seasonIds);
    }

    //gestisce lo stato di attività della spiaggia nella piattaforma
    //(verifica se è conforme all'attivazione, ovvero se ha tutti i campi compilati)
    public void setActive(boolean active) {
        if (active) {
            if (beachGeneral == null || beachInventory == null || beachServices == null || parking == null || seasonIds == null || seasonIds.isEmpty()) {
                throw new IllegalStateException("ERROR: beach can be set to active only if owner, general, inventory, services, parking and season(s) are set");
            }
        }
        this.active = active;
    }

    //metodi update per vari attributi
    public void updateGeneralInfo(BeachGeneral newGeneral) {
        checkGeneralInfo(newGeneral);
        this.beachGeneral = newGeneral;
    }

    public void updateInventory(BeachInventory newInventory) {
        checkInventory(newInventory);
        this.beachInventory = newInventory;
    }

    public void updateServices(BeachServices newServices) {
        checkServices(newServices);
        this.beachServices = newServices;
    }

    public void updateParking(Parking newParking) {
        checkParking(newParking);
        this.parking = newParking;
    }


    //---- METODI CKECKERS ----
    private void checkExtraInfo(String extraInfo) {
        if (extraInfo.length() > 512) throw new IllegalArgumentException("ERROR: extraInfo cannot exceed 512 characters");
    }
    private void checkOwnerId(Integer ownerId) {
        //controllo prima se null, poi se ID è valido
        //(se arrivo al secondo if con un valore null, ho NullPointerException)
        if (ownerId == null) throw new IllegalArgumentException("ERROR: ownerId cannot be null");
        if (ownerId <= 0) throw new IllegalArgumentException("ERROR: ownerId not valid");
    }
    private void checkSeasonId(Integer seasonId) {
        if (seasonId == null) throw new IllegalArgumentException("ERROR: seasonId cannot be null");
        if (seasonId <= 0) throw new IllegalArgumentException("ERROR: seasonId not valid");
    }
    private void checkSeasonIds(List<Integer> seasonIds) {
        //controllo lista se vuota
        if (seasonIds == null || seasonIds.isEmpty()) throw new IllegalArgumentException("ERROR: list not valid");
        //controllo integrità dei valori inseriti nella stagione
        for (Integer seasonId : seasonIds) {
            if (seasonId == null || seasonId <= 0) throw new IllegalArgumentException("ERROR: at least one seasonId in the list is not valid");
        }
    }
    private void checkGeneralInfo(BeachGeneral newGeneral) {
        if (newGeneral == null) throw new IllegalArgumentException("ERROR: General info cannot be reverted to null");
    }
    private void checkInventory(BeachInventory newInventory) {
        if (newInventory == null) throw new IllegalArgumentException("ERROR: Inventory cannot be reverted to null");
    }
    private void checkServices(BeachServices newServices) {
        if (newServices == null) throw new IllegalArgumentException("ERROR: Services cannot be reverted to null");
    }
    private void checkParking(Parking newParking) {
        if (newParking == null) throw new IllegalArgumentException("ERROR: Parking cannot be reverted to null");
    }
}