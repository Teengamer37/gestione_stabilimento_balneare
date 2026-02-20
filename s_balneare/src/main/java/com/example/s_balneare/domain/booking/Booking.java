package com.example.s_balneare.domain.booking;

import com.example.s_balneare.domain.layout.SpotType;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Booking {
    private final UUID id;
    private final UUID beachID;
    private final UUID customerID;
    private final LocalDate date;

    private final List<UUID> spotIDs;
    private int extraSdraio;
    private int extraLettini;
    private int extraSedie;

    private BookingStatus status;

    public Booking(UUID id, UUID beachID, UUID customerID, LocalDate date, List<UUID> spotIDs, int extraSdraio, int extraLettini, int extraSedie) {
        if (spotIDs.isEmpty()) {
            throw new IllegalArgumentException("ERROR: at least one spot must be selected for booking " + id);
        }
        if (extraSdraio < 0 || extraLettini < 0 || extraSedie < 0) {
            throw new IllegalArgumentException("ERROR: extra quantity must be >=0 for booking " + id);
        }

        this.id = id;
        this.beachID = beachID;
        this.customerID = customerID;
        this.date = date;
        this.spotIDs = spotIDs;
        this.extraSdraio = extraSdraio;
        this.extraLettini = extraLettini;
        this.extraSedie = extraSedie;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBeachID() {
        return beachID;
    }

    public UUID getCustomerID() {
        return customerID;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<UUID> getSpotIDs() {
        return spotIDs;
    }

    public int getExtraSdraio() {
        return extraSdraio;
    }

    public void setExtraSdraio(int extraSdraio) {
        if (extraSdraio < 0) throw new IllegalArgumentException("ERROR: extra quantity must be >=0 for booking " + id);
        this.extraSdraio = extraSdraio;
    }

    public int getExtraLettini() {
        return extraLettini;
    }

    public void setExtraLettini(int extraLettini) {
        if (extraLettini < 0) throw new IllegalArgumentException("ERROR: extra quantity must be >=0 for booking " + id);
        this.extraLettini = extraLettini;
    }

    public int getExtraSedie() {
        return extraSedie;
    }

    public void setExtraSedie(int extraSedie) {
        if (extraSedie < 0) throw new IllegalArgumentException("ERROR: extra quantity must be >=0 for booking " + id);
        this.extraSedie = extraSedie;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public float getTotalPrice() {
        float totalPrice = 0;
        Map<SpotType, Integer> numSpots = getNumberOfSpotTypes();

        /* TBD: fare funzione ricavo prezzi
           Formula: prendi prezzi standard stagione,
           prezzo ombrellone * numSpots.get(OMBRELLONE) +
           prezzo tende * numSpots.get(TENDE) +
           prezzo sdraio * numero sdraio +
           prezzo lettini * numero lettini +
           prezzo sedie * numero sedie
         */

        return totalPrice;
    }

    private Map<SpotType, Integer> getNumberOfSpotTypes() {
        Map<SpotType, Integer> counts = new EnumMap<>(SpotType.class);
        for (SpotType type : SpotType.values()) {
            counts.put(type, 0);
        }

        /*
            TBD: DA IMPLEMENTARE SUCCESSIVAMENTE A DATABASE FINITO
         */

        return counts;
    }
}
