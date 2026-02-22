package com.example.s_balneare.domain.booking;

import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.layout.Spot;
import com.example.s_balneare.domain.layout.SpotType;
import com.example.s_balneare.domain.user.CustomerUser;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Booking {
    private final int id;
    private final Beach beach;
    private final CustomerUser customer;
    private final LocalDate date;

    private final List<Spot> spots;
    private int extraSdraio;
    private int extraLettini;
    private int extraSedie;
    private int changingRoom;

    private BookingStatus status;

    public Booking(int id, Beach beach, CustomerUser customer, LocalDate date, List<Spot> spots, int extraSdraio, int extraLettini, int extraSedie, int changingRoom) {
        if (spots.isEmpty()) {
            throw new IllegalArgumentException("ERROR: at least one spot must be selected for booking " + id);
        }
        if (extraSdraio < 0 || extraLettini < 0 || extraSedie < 0 || changingRoom < 0) {
            throw new IllegalArgumentException("ERROR: extra quantity must be >=0 for booking " + id);
        }

        this.id = id;
        this.beach = beach;
        this.customer = customer;
        this.date = date;
        this.spots = spots;
        this.extraSdraio = extraSdraio;
        this.extraLettini = extraLettini;
        this.extraSedie = extraSedie;
        this.changingRoom = changingRoom;
    }

    public int getId() {
        return id;
    }

    public Beach getBeach() {
        return beach;
    }

    public CustomerUser getCustomer() {
        return customer;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<Spot> getSpots() {
        return spots;
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

    public int getChangingRoom() {return changingRoom;}

    public void setChangingRoom(int changingRoom) {
        if (changingRoom < 0) throw new IllegalArgumentException("ERROR: quantity must be >=0 for booking " + id);
        this.changingRoom = changingRoom;}

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