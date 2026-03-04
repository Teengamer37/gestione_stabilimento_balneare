package com.example.s_balneare.domain.beach;

import java.util.ArrayList;
import java.util.List;

public class Beach {
    private final Integer id;
    private int ownerId;

    private final int addressId;
    private BeachGeneral beachGeneral;
    private BeachInventory beachInventory;
    private BeachServices beachServices;
    private Parking parking;
    private final List<Integer> seasonIds;

    private String extraInfo;
    private boolean active;

    //costruttore
    public Beach(Integer id, int ownerId, int addressId, BeachGeneral beachGeneral, BeachInventory beachInventory, BeachServices beachServices, Parking parking, String extraInfo, List<Integer> seasonIds, boolean active) {
        this.id = id;
        setOwnerId(ownerId);
        this.addressId = addressId;
        this.beachGeneral = beachGeneral;
        this.beachInventory = beachInventory;
        this.beachServices = beachServices;
        this.parking = parking;
        editExtraInfo(extraInfo);
        if (seasonIds == null || seasonIds.isEmpty()) this.seasonIds = new ArrayList<Integer>();
        else this.seasonIds = new ArrayList<>(seasonIds);
        setActive(active);
    }

    //getters
    public Integer getId() {
        return id;
    }
    public int getOwnerId() {
        return ownerId;
    }
    public int getAddressId() {
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

    //permette di modificare la sezione di info extra
    public void editExtraInfo(String info) {
        if (info == null) extraInfo = "";
        else if (info.length() > 512) throw new IllegalArgumentException("ERROR: extraInfo cannot exceed 512 characters");
        else extraInfo = info;
    }

    /*
        L'aggiunta di una caratteristica si fa passando un ID valido (> di 0)
        la rimozione della stessa si fa mettendo a 0 l'ID corrispondente
     */

    //permette di aggiungere/rimuovere un owner alla/dalla spiaggia
    public void setOwnerId(int ownerId) {
        if (ownerId < 0) throw new IllegalArgumentException("ERROR: ownerId not valid");
        this.ownerId = ownerId;
    }

    /*
        Nella parte delle stagioni, la regola precedente non è valida: l'ID deve essere valido sin dall'inserimento/rimozione
     */

    //aggiunge una stagione
    public void addSeason(int seasonId) {
        if (seasonId <= 0) throw new IllegalArgumentException("ERROR: seasonId not valid");
        seasonIds.add(seasonId);
    }

    //aggiunge una lista di stagioni
    public void addSeasons(List<Integer> seasonIds) {
        if (seasonIds == null || seasonIds.isEmpty()) throw new IllegalArgumentException("ERROR: no list to add to seasons");
        for (int seasonId : seasonIds) {
            if (seasonId <= 0) throw new IllegalArgumentException("ERROR: at least one seasonId in the list is not valid");
        }
        this.seasonIds.addAll(seasonIds);
    }

    //rimuove una stagione
    public void removeSeason (int seasonId) {
        if (seasonId <= 0) throw new IllegalArgumentException("ERROR: seasonId not valid");
        if (seasonIds.contains(seasonId)) seasonIds.remove(Integer.valueOf(seasonId));
        else throw new IllegalArgumentException("ERROR: seasonId not found in seasons");
    }

    //rimuove una lista di stagioni
    public void removeSeasons (List<Integer> seasonIds) {
        if (seasonIds == null || seasonIds.isEmpty()) throw new IllegalArgumentException("ERROR: no list to remove from seasons");
        for (int seasonId : seasonIds) {
            if (seasonId <= 0) throw new IllegalArgumentException("ERROR: at least one seasonId in the list is not valid");
            if (!this.seasonIds.contains(seasonId)) throw new IllegalArgumentException("ERROR: at least one seasonId in the list is not found in seasons");
        }
        this.seasonIds.removeAll(seasonIds);
    }

    //gestisce lo stato di attività della spiaggia nella piattaforma (verifica se è conforme all'attivazione,
    //ovvero se ha tutti i campi compilati)
    public void setActive(boolean active) {
        if (active) {
            if (ownerId == 0 || beachGeneral == null || beachInventory == null || beachServices == null || parking == null || seasonIds == null || seasonIds.isEmpty()) {
                throw new IllegalStateException("ERROR: beach can be set to active only if owner, general, inventory, services, parking and season(s) are set");
            }
        }
        this.active = active;
    }

    //metodi update per vari attributi
    public void updateGeneralInfo(BeachGeneral newGeneral) {
        if (newGeneral == null) throw new IllegalArgumentException("ERROR: General info cannot be null");
        this.beachGeneral = newGeneral;
    }

    public void updateInventory(BeachInventory newInventory) {
        if (newInventory == null) throw new IllegalArgumentException("ERROR: Inventory cannot be null");
        this.beachInventory = newInventory;
    }

    public void updateServices(BeachServices newServices) {
        if (newServices == null) throw new IllegalArgumentException("ERROR: Services cannot be null");
        this.beachServices = newServices;
    }

    public void updateParking(Parking newParking) {
        if (newParking == null) throw new IllegalArgumentException("ERROR: Parking cannot be null");
        this.parking = newParking;
    }
}