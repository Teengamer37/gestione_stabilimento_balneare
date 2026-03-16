package com.example.s_balneare.domain.beach;

import com.example.s_balneare.domain.layout.Zone;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/// Definisce un'istanza di una spiaggia
public class Beach {
    //attributi
    private final Integer id;
    private final Integer addressId;
    private final List<Season> seasons;
    private final List<Zone> zones;
    private Integer ownerId;
    private BeachGeneral beachGeneral;
    private BeachInventory beachInventory;
    private BeachServices beachServices;
    private Parking parking;
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

        if (zones == null || zones.isEmpty()) this.zones = new ArrayList<>();
        else this.zones = new ArrayList<>(zones);

        this.seasons = new ArrayList<>();
        if (seasons != null && !seasons.isEmpty()) {
            addSeasons(seasons);
        }

        this.closed = closed;
        if (closed) {
            this.active = false;
        } else {
            this.active = active;
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
     * Gestisce lo stato di attività della spiaggia nella piattaforma
     * (verifica se è conforme all'attivazione, ovvero se ha tutti i campi compilati).
     *
     * @param active nuovo stato di attività
     */
    public void setActive(boolean active) {
        if (active) {
            validateActivationRequirements();
        }
        this.active = active;
    }

    /**
     * Permette di modificare la sezione di info extra.
     *
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
     * Permette di aggiungere/rimuovere un owner alla/dalla spiaggia.
     *
     * @param ownerId nuovo proprietario
     */
    public void updateOwnerId(Integer ownerId) {
        checkNotActive("update owner info");
        checkOwnerId(ownerId);
        this.ownerId = ownerId;
    }

    /**
     * Aggiunge una stagione.
     *
     * @param season nuova stagione
     */
    public void addSeason(Season season) {
        checkSeason(season);
        checkSeasonOverlap(season, null);
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
     * Aggiunge una lista di stagioni.
     *
     * @param seasons nuove stagioni
     */
    public void addSeasons(List<Season> seasons) {
        checkSeasons(seasons);
        for (Season s : seasons) {
            checkSeasonOverlap(s, null);
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
     * Aggiorna una stagione esistente rispettando i vincoli di immutabilità finanziaria e temporale.
     *
     * @param targetSeasonName nome della stagione da aggiornare
     * @param newEndDate       nuova data di fine
     * @throws IllegalArgumentException se ci sono parametri non validi
     */
    public void updateSeason(String targetSeasonName, LocalDate newEndDate) {
        checkNotActive("update season");

        //trovo la stagione
        Season existingSeason = seasons.stream()
                .filter(s -> s.name().equals(targetSeasonName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ERROR: season not found: " + targetSeasonName));

        //check nuova data di fine
        if (newEndDate.isBefore(existingSeason.endDate())) {
            throw new IllegalArgumentException("ERROR: cannot shorten an existing season");
        }

        //creo una nuova stagione con la nuova data di fine
        Season updatedSeason = existingSeason.withDates(existingSeason.startDate(), newEndDate);

        //controllo se la stagione va a interpolarsi con le altre
        checkSeasonOverlap(updatedSeason, existingSeason);

        //sostituisco la vecchia stagione con la nuova
        seasons.remove(existingSeason);
        seasons.add(updatedSeason);
    }

    /**
     * Rimuove una stagione.
     *
     * @param seasonName nome della stagione da rimuovere
     * @throws IllegalArgumentException se la stagione non esiste nell’oggetto
     */
    public void removeSeason(String seasonName) {
        checkNotActive("remove season");

        boolean removed = seasons.removeIf(s -> s.name().equals(seasonName));
        if (!removed) {
            throw new IllegalArgumentException("ERROR: Season not found");
        }
    }

    /**
     * Rimuove una lista di stagioni.
     *
     * @param seasonNames nome delle stagioni da rimuovere
     * @throws IllegalArgumentException se almeno una delle stagioni non esiste nell’oggetto
     */
    public void removeSeasons(List<String> seasonNames) {
        checkNotActive("remove seasons");

        //trovo le stagioni da rimuovere
        List<Season> seasonsToDelete = this.seasons.stream()
                .filter(season -> seasonNames.contains(season.name()))
                .toList();

        //controllo se ho trovato tutte le stagioni da rimuovere
        if (seasonsToDelete.size() != seasonNames.size()) {
            throw new IllegalArgumentException("ERROR: at least one season name in the list was not found");
        }

        //rimuovo
        this.seasons.removeAll(seasonsToDelete);
    }

    /**
     * Aggiunge o aggiorna una zona.
     *
     * @param zone zona da aggiungere/aggiornare
     */
    public void addZone(Zone zone) {
        checkNotActive("add/update zone");
        checkZone(zone);

        //rimuove la zona esistente se presente, per poi aggiungerla (aggiornamento)
        boolean zoneExists = zones.stream().anyMatch(z -> z.name().equals(zone.name()));
        if (zoneExists) {
            checkIfZoneIsLockedBySeason(zone.name());
            zones.removeIf(z -> z.name().equals(zone.name()));
        }

        zones.add(zone);
    }

    /**
     * Aggiunge o aggiorna una lista di zone.
     *
     * @param newZones zone da aggiungere/aggiornare
     */
    public void addZones(List<Zone> newZones) {
        checkNotActive("add/update zones");
        checkZones(newZones);

        for (Zone newZone : newZones) {
            //rimuove la zona esistente se presente, per poi aggiungerla (aggiornamento)
            boolean zoneExists = zones.stream().anyMatch(z -> z.name().equals(newZone.name()));
            if (zoneExists) {
                checkIfZoneIsLockedBySeason(newZone.name());
                this.zones.removeIf(z -> z.name().equals(newZone.name()));
            }
            this.zones.add(newZone);
        }
    }

    /**
     * Rinomina una zona esistente.
     *
     * @param oldName Il nome attuale della zona
     * @param newName Il nuovo nome desiderato
     * @throws IllegalArgumentException se il nuovo nome della zona è già stato preso da una zona già salvata nella spiaggia
     * oppure se il nome della zona da modificare non è stato trovato nel database
     */
    public void renameZone(String oldName, String newName) {
        checkNotActive("rename zone");

        //controllo parametro
        if (newName == null || newName.isBlank() || newName.length() > 50) {
            throw new IllegalArgumentException("ERROR: invalid new zone name");
        }

        //passo 1: verifico unicità nuovo nome
        boolean newNameExists = zones.stream().anyMatch(z -> z.name().equals(newName));
        if (newNameExists) {
            throw new IllegalArgumentException("ERROR: zone with the name '" + newName + "' already exists in DB");
        }

        //passo 2: ricavo zona da modificare dal DB
        Zone oldZone = zones.stream()
                .filter(z -> z.name().equals(oldName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ERROR: zone '" + oldName + "' not found."));

        //passo 3: controllo che la zona non sia usata da una stagione
        checkIfZoneIsLockedBySeason(oldName);

        //passo 4: aggiorno il nome e salvo
        Zone updatedZone = oldZone.withName(newName);
        zones.remove(oldZone);
        zones.add(updatedZone);
    }

    /**
     * Rimuove una zona.
     *
     * @param zone zona da rimuovere
     * @throws IllegalArgumentException se la zona non esiste nell’oggetto
     */
    public void removeZone(Zone zone) {
        checkNotActive("remove zone");
        checkZone(zone);

        checkIfZoneIsLockedBySeason(zone.name());

        if (!zones.remove(zone)) {
            throw new IllegalArgumentException("ERROR: zone not found in zones list");
        }
    }

    /**
     * Rimuove una lista di zone.
     *
     * @param zonesToRemove zone da rimuovere
     * @throws IllegalArgumentException se almeno una delle zone non esiste nell’oggetto
     */
    public void removeZones(List<Zone> zonesToRemove) {
        checkNotActive("remove zones");
        checkZones(zonesToRemove);

        for (Zone zone : zonesToRemove) {
            checkIfZoneIsLockedBySeason(zone.name());
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
     * Verifica se la spiaggia ha tutti gli attributi settati.
     *
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
     * e lancia il messaggio dettagliato.
     *
     * @throws IllegalStateException con tutti i dati mancanti
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
            hasValidSeason = seasons.stream().anyMatch(s -> s.endDate().isAfter(today));
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
     * Chiude definitivamente una spiaggia.
     *
     * @throws IllegalStateException se la spiaggia è già chiusa
     */
    public void closeBeach() {
        if (this.closed) {
            throw new IllegalStateException("ERROR: Beach is already closed");
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
        if (extraInfo.length() > 512)
            throw new IllegalArgumentException("ERROR: extraInfo cannot exceed 512 characters");
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
        if (newGeneral == null) throw new IllegalArgumentException("ERROR: general info cannot be reverted to null");
    }

    private void checkInventory(BeachInventory newInventory) {
        if (newInventory == null) throw new IllegalArgumentException("ERROR: inventory cannot be reverted to null");
    }

    private void checkServices(BeachServices newServices) {
        if (newServices == null) throw new IllegalArgumentException("ERROR: services cannot be reverted to null");
    }

    private void checkParking(Parking newParking) {
        if (newParking == null) throw new IllegalArgumentException("ERROR: parking cannot be reverted to null");
    }

    private void checkNotActive(String action) {
        if (this.closed) {
            throw new IllegalStateException("ERROR: cannot " + action + " because the beach is permanently CLOSED");
        }
        if (this.active) {
            throw new IllegalStateException("ERROR: cannot " + action + " while the beach is ACTIVE");
        }
    }

    private void checkSeasonOverlap(Season newSeason, Season seasonToIgnore) {
        for (Season existing : this.seasons) {
            if (seasonToIgnore != null && existing.name().equals(seasonToIgnore.name())) continue;

            if (!newSeason.endDate().isBefore(existing.startDate()) &&
                    !newSeason.startDate().isAfter(existing.endDate())) {
                throw new IllegalArgumentException("ERROR: new period overlaps with existing season: " + existing.name());
            }
        }
    }

    private void checkIfZoneIsLockedBySeason(String zoneName) {
        boolean isZoneInSeason = seasons.stream()
                .anyMatch(s -> s.zoneTariffs().stream()
                        .anyMatch(zt -> zt.zoneName().equals(zoneName)));
        if (isZoneInSeason) {
            throw new IllegalStateException("ERROR: zone is part of one or more seasons");
        }
    }
}