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
    private final List<Season> seasons;

    private String extraInfo;
    private boolean active;


    //costruttore
    public Beach(Integer id, Integer ownerId, Integer addressId, BeachGeneral beachGeneral, BeachInventory beachInventory,
                 BeachServices beachServices, Parking parking, String extraInfo, List<Season> seasons, boolean active) {
        this.id = id;
        updateOwnerId(ownerId);
        if (addressId == null) throw new IllegalArgumentException("ERROR: addressId cannot be null");
        this.addressId = addressId;
        this.beachGeneral = beachGeneral;
        this.beachInventory = beachInventory;
        this.beachServices = beachServices;
        this.parking = parking;
        updateExtraInfo(extraInfo);
        if (seasons == null || seasons.isEmpty()) this.seasons = new ArrayList<Season>();
        else this.seasons = new ArrayList<>(seasons);
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
    public List<Season> getSeasons() {
        return seasons;
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
    public void addSeason(Season season) {
        checkSeason(season);
        seasons.add(season);
    }

    //aggiunge una lista di stagioni
    public void addSeasons(List<Season> seasons) {
        checkSeasons(seasons);
        this.seasons.addAll(seasons);
    }

    //rimuove una stagione
    public void removeSeason (Season season) {
        checkSeason(season);
        //tentativo eliminazione
        if (seasons.contains(season)) seasons.remove(season);
        else throw new IllegalArgumentException("ERROR: seasonId not found in seasons");
    }

    //rimuove una lista di stagioni
    public void removeSeasons (List<Season> seasons) {
        checkSeasons(seasons);
        //mi assicuro che TUTTE le stagioni nella lista siano presenti nella spiaggia
        for (Season season : seasons) {
            if (!this.seasons.contains(season)) throw new IllegalArgumentException("ERROR: at least one seasonId in the list is not found in seasons");
        }
        this.seasons.removeAll(seasons);
    }

    //gestisce lo stato di attività della spiaggia nella piattaforma
    //(verifica se è conforme all'attivazione, ovvero se ha tutti i campi compilati)
    public void setActive(boolean active) {
        if (active) {
            if (beachGeneral == null || beachInventory == null || beachServices == null || parking == null || seasons == null || seasons.isEmpty()) {
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
    private void checkSeason(Season season) {
        if (season == null) throw new IllegalArgumentException("ERROR: season cannot be null");
    }
    private void checkSeasons(List<Season> seasons) {
        //controllo lista se vuota
        if (seasons == null || seasons.isEmpty()) throw new IllegalArgumentException("ERROR: list not valid");
        //controllo integrità dei valori inseriti nella stagione
        for (Season season : seasons) {
            if (season == null) throw new IllegalArgumentException("ERROR: at least one season in the list is null");
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