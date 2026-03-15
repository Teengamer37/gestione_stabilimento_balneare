package com.example.s_balneare.domain.beach;

import com.example.s_balneare.domain.layout.Zone;

import java.time.LocalDate;
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
    private final List<Zone> zones;

    private String extraInfo;
    private boolean active;
    private boolean closed;


    //costruttore
    public Beach(Integer id, Integer ownerId, Integer addressId, BeachGeneral beachGeneral, BeachInventory beachInventory,
                 BeachServices beachServices, Parking parking, String extraInfo, List<Season> seasons, List<Zone> zones, boolean active, boolean closed) {
        this.id = id;
        updateOwnerId(ownerId);
        if (addressId == null) throw new IllegalArgumentException("ERROR: addressId cannot be null");
        this.addressId = addressId;
        if (beachGeneral == null) throw new IllegalArgumentException("ERROR: beachGeneral cannot be null");
        this.beachGeneral = beachGeneral;
        this.beachInventory = beachInventory;
        this.beachServices = beachServices;
        this.parking = parking;
        updateExtraInfo(extraInfo);

        if (zones == null || zones.isEmpty()) this.zones = new ArrayList<Zone>();
        else this.zones = new ArrayList<>(zones);

        this.seasons = new ArrayList<Season>();
        if (seasons != null && !seasons.isEmpty()) {
            addSeasons(seasons);
        }

        this.closed = closed;
        if (closed) {
            this.active = false;
        } else {
            setActive(active);
        }
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
    public List<Zone> getZones() {
        return zones;
    }
    public boolean isActive() {
        return active;
    }
    public boolean isClosed() {
        return closed;
    }


    //---- METODI DI BUSINESS ----
    /**
     * Permette di modificare la sezione di info extra
     * @param info nuova sezione di extraInfo
     */
    public void updateExtraInfo(String info) {
        checkNotActive("update extra info");
        if (info == null) extraInfo = "";
        else {
            checkExtraInfo(info);
            extraInfo = info;
        }
    }

    /**
     * Permette di aggiungere/rimuovere un owner alla/dalla spiaggia
     * @param ownerId nuovo proprietario
     */
    public void updateOwnerId(Integer ownerId) {
        checkNotActive("update owner info");
        checkOwnerId(ownerId);
        this.ownerId = ownerId;
    }

    /**
     * Aggiunge una stagione
     * @param season nuova stagione
     */
    public void addSeason(Season season) {
        checkSeason(season);
        checkSeasonOverlap(season);
        seasons.add(season);
        
        //aggiunge automaticamente le zone definite nella stagione se non esistono già
        for (ZoneTariff tariff : season.zoneTariffs()) {
            //cerco se la Zone definita in ZoneTariff esiste già dentro zones
            boolean zoneExists = zones.stream()
                    .anyMatch(z -> z.name().equals(tariff.zoneName()));

            //se non esiste, allora la aggiungo dentro zones
            if (!zoneExists) {
                zones.add(Zone.create(tariff.zoneName()));
            }
        }
    }

    /**
     * Aggiunge una lista di stagioni
     * @param seasons nuove stagioni
     */
    public void addSeasons(List<Season> seasons) {
        checkSeasons(seasons);
        for (Season s : seasons) {
            checkSeasonOverlap(s);
            this.seasons.add(s);
        }

        //aggiunge automaticamente le zone definite nella stagione se non esistono già
        for (Season season : seasons) {
            for (ZoneTariff tariff : season.zoneTariffs()) {
                //cerco se la Zone definita in ZoneTariff esiste già dentro zones
                boolean zoneExists = zones.stream()
                        .anyMatch(z -> z.name().equals(tariff.zoneName()));

                //se non esiste, allora la aggiungo dentro zones
                if (!zoneExists) {
                    zones.add(Zone.create(tariff.zoneName()));
                }
            }
        }
    }

    /**
     * Rimuove una stagione
     * @param season stagione da rimuovere
     */
    public void removeSeason(Season season) {
        checkSeason(season);
        //tentativo eliminazione
        if (seasons.contains(season)) seasons.remove(season);
        else throw new IllegalArgumentException("ERROR: seasonId not found in seasons");
    }

    /**
     * Rimuove una lista di stagioni
     * @param seasons stagioni da rimuovere
     */
    public void removeSeasons(List<Season> seasons) {
        checkSeasons(seasons);
        //mi assicuro che TUTTE le stagioni nella lista siano presenti nella spiaggia
        for (Season season : seasons) {
            if (!this.seasons.contains(season)) throw new IllegalArgumentException("ERROR: at least one seasonId in the list is not found in seasons");
        }
        this.seasons.removeAll(seasons);
    }

    /**
     * Aggiunge o aggiorna una zona
     * @param zone zona da aggiungere/aggiornare
     */
    public void addZone(Zone zone) {
        checkZone(zone);
        //rimuove la zona esistente se presente, per poi aggiungerla (aggiornamento)
        zones.removeIf(z -> z.name().equals(zone.name()));
        zones.add(zone);
    }

    /**
     * Aggiunge o aggiorna una lista di zone
     * @param newZones zone da aggiungere/aggiornare
     */
    public void addZones(List<Zone> newZones) {
        checkZones(newZones);
        for (Zone newZone : newZones) {
            //rimuove la zona esistente se presente, per poi aggiungerla (aggiornamento)
            this.zones.removeIf(z -> z.name().equals(newZone.name()));
            this.zones.add(newZone);
        }
    }

    /**
     * Rimuove una zona
     * @param zone zona da rimuovere
     */
    public void removeZone(Zone zone) {
        checkZone(zone);
        //controlla se la zona è presente in una qualsiasi stagione
        boolean isZoneInSeason = seasons.stream()
                .anyMatch(s -> s.zoneTariffs().stream()
                        .anyMatch(zt -> zt.zoneName().equals(zone.name())));
        if (isZoneInSeason) {
            throw new IllegalStateException("ERROR: cannot remove a zZone that is part of a Season");
        }
        if (!zones.remove(zone)) {
            throw new IllegalArgumentException("ERROR: Zone not found in Zones");
        }
    }

    /**
     * Rimuove una lista di zone
     * @param zonesToRemove zone da rimuovere
     */
    public void removeZones(List<Zone> zonesToRemove) {
        checkZones(zonesToRemove);
        for (Zone zone : zonesToRemove) {
            //controlla se la zona è presente in una qualsiasi stagione
            boolean isZoneInSeason = seasons.stream()
                    .anyMatch(s -> s.zoneTariffs().stream()
                            .anyMatch(zt -> zt.zoneName().equals(zone.name())));
            if (isZoneInSeason) {
                throw new IllegalStateException("ERROR: cannot remove a Zone that is part of a Season");
            }
        }
        //mi assicuro che tutte le Zone siano riferiti alla Beach
        for (Zone zone : zonesToRemove) {
            if (!this.zones.contains(zone)) {
                throw new IllegalArgumentException("ERROR: at least one Zone in the list is not found in zones");
            }
        }
        this.zones.removeAll(zonesToRemove);
    }

    /**
     * Verifica se la spiaggia ha tutti gli attributi settati
     * @return TRUE se possiede tutti i campi completati, FALSE altrimenti
     */
    public boolean isFullyConfigured() {
        try {
            validateActivationRequirements();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Helper privato che raccoglie esattamente i dati mancanti
     * e lancia il messaggio dettagliato
     */
    private void validateActivationRequirements() {
        List<String> missing = new ArrayList<>();

        //verifico dati generici/inventario/servizi/parcheggio
        if (beachGeneral == null) missing.add("general info");
        if (beachInventory == null) missing.add("inventory");
        if (beachServices == null) missing.add("services");
        if (parking == null) missing.add("parking");

        //verifico zone
        if (zones == null || zones.isEmpty()) {
            missing.add("at least one zone");
        }

        //verifico che ci sia almeno una stagione futura o in corso
        boolean hasValidSeason = false;
        if (seasons != null && !seasons.isEmpty()) {
            LocalDate today = LocalDate.now();
            hasValidSeason = seasons.stream().anyMatch(s -> !s.endDate().isBefore(today));
        }
        if (!hasValidSeason) {
            missing.add("at least one upcoming or current season");
        }

        //se la lista di errori non è vuota, lancio eccezione
        if (!missing.isEmpty()) {
            String missingList = String.join(", ", missing);
            throw new IllegalStateException("ERROR: cannot activate the Beach. Missing data: " + missingList);
        }
    }

    /**
     * Gestisce lo stato di attività della spiaggia nella piattaforma
     * (verifica se è conforme all'attivazione, ovvero se ha tutti i campi compilati)
     * @param active nuovo stato di attività
     */
    public void setActive(boolean active) {
        if (active) {
            validateActivationRequirements();
        }
        this.active = active;
    }

    /**
     * Chiude definitivamente una spiaggia
     */
    public void closeBeach() {
        if (this.closed) {
            throw new IllegalStateException("ERROR: Beach is already closed.");
        }
        this.closed = true;
        this.active = false;
    }

    //metodi update per vari attributi
    public void updateGeneralInfo(BeachGeneral newGeneral) {
        checkNotActive("update general info");
        checkGeneralInfo(newGeneral);
        this.beachGeneral = newGeneral;
    }

    public void updateInventory(BeachInventory newInventory) {
        checkNotActive("update inventory");
        checkInventory(newInventory);
        this.beachInventory = newInventory;
    }

    public void updateServices(BeachServices newServices) {
        checkNotActive("update services");
        checkServices(newServices);
        this.beachServices = newServices;
    }

    public void updateParking(Parking newParking) {
        checkNotActive("update parking");
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
    private void checkZone(Zone zone) {
        if (zone == null) throw new IllegalArgumentException("ERROR: zone cannot be null");
    }
    private void checkZones(List<Zone> zones) {
        //controllo lista se vuota
        if (zones == null || zones.isEmpty()) throw new IllegalArgumentException("ERROR: list not valid");
        //controllo integrità dei valori inseriti nella zona
        for (Zone zone : zones) {
            if (zone == null) throw new IllegalArgumentException("ERROR: at least one zone in the list is null");
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
    private void checkNotActive(String action) {
        if (this.closed) {
            throw new IllegalStateException("ERROR: Cannot " + action + " because the beach is permanently CLOSED.");
        }
        if (this.active) {
            throw new IllegalStateException("ERROR: cannot " + action + " while the beach is ACTIVE");
        }
    }
    private void checkSeasonOverlap(Season newSeason) {
        for (Season existing : this.seasons) {
            if (!newSeason.endDate().isBefore(existing.startDate()) &&
                    !newSeason.startDate().isAfter(existing.endDate())) {
                throw new IllegalArgumentException("ERROR: Season dates overlap with existing season: " + existing.name());
            }
        }
    }
}