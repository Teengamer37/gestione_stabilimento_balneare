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
    //dati booking
    private final int id;
    private final Beach beach;
    private final CustomerUser customer;
    private final LocalDate date;

    //oggetti booking
    private final List<Spot> spots;
    private int extraSdraio;
    private int extraLettini;
    private int extraSedie;
    private int camerini;

    //stato booking
    private BookingStatus status;

    //costruttore fatto con Builder Pattern
    private Booking(BookingBuilder builder) {
        //check dati final
        if (builder.beach == null) throw new IllegalArgumentException("ERROR: beach cannot be null for booking " + builder.id);
        if (builder.customer == null) throw new IllegalArgumentException("ERROR: customer cannot be null for booking " + builder.id);
        if (builder.date == null) throw new IllegalArgumentException("ERROR: date cannot be null for booking " + builder.id);
        if (builder.spots == null || builder.spots.isEmpty()) throw new IllegalArgumentException("ERROR: at least one spot must be selected for booking " + builder.id);

        //check integrità interi
        if (builder.extraSdraio < 0 || builder.extraLettini < 0 || builder.extraSedie < 0 || builder.camerini < 0) {
            throw new IllegalArgumentException("ERROR: extra quantity must be >=0 for booking " + builder.id);
        }

        this.id = builder.id;
        this.beach = builder.beach;
        this.customer = builder.customer;
        this.date = builder.date;
        this.spots = builder.spots;
        this.extraSdraio = builder.extraSdraio;
        this.extraLettini = builder.extraLettini;
        this.extraSedie = builder.extraSedie;
        this.camerini = builder.camerini;
        this.status = BookingStatus.PENDING;
    }

    //builder pattern
    public static class BookingBuilder {
        private final int id;
        private final Beach beach;
        private final CustomerUser customer;
        private final LocalDate date;

        private final List<Spot> spots;
        private int extraSdraio;
        private int extraLettini;
        private int extraSedie;
        private int camerini;

        public BookingBuilder(int id, Beach beach, CustomerUser customer, LocalDate date, List<Spot> spots) {
            this.id = id;
            this.beach = beach;
            this.customer = customer;
            this.date = date;
            this.spots = spots;
        }

        //adders per attributi opzionali
        public BookingBuilder extraSdraio(int quantity) {
            extraSdraio += quantity;
            return this;
        }
        public BookingBuilder extraLettini(int quantity) {
            extraLettini += quantity;
            return this;
        }
        public BookingBuilder extraSedie(int quantity) {
            extraSedie += quantity;
            return this;
        }
        public BookingBuilder camerini(int quantity) {
            camerini += quantity;
            return this;
        }

        //build
        public Booking build() {
            return new Booking(this);
        }
    }

    // getters (NO SETTERS)
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
    public int getExtraLettini() {
        return extraLettini;
    }
    public int getExtraSedie() {
        return extraSedie;
    }
    public int getCamerini() {return camerini;}
    public BookingStatus getStatus() {
        return status;
    }

    //conferma booking solo se status == PENDING
    public void confirmBooking() {
        if (status != BookingStatus.PENDING) {
            throw new IllegalStateException("ERROR: booking " + id + " has to be pending in order to be confirmed");
        }
        status = BookingStatus.CONFIRMED;
    }

    //rifiuta booking solo se status == PENDING
    public void rejectBooking() {
        if (status == BookingStatus.PENDING) {
            throw new IllegalStateException("ERROR: booking " + id + " has to be pending in order to be rejected");
        }
        status = BookingStatus.REJECTED;
    }

    //cancella booking solo se status == PENDING || CONFIRMED
    public void cancelBooking() {
        if (status == BookingStatus.REJECTED || status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("ERROR: booking " + id + " has to be either pending or confirmed in order to be cancelled");
        }
    }

    //aggiungi extra sdraio con controlli
    public void addExtraSdraio(int quantity, int availableSdraio) {
        if (status == BookingStatus.REJECTED || status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("ERROR: booking " + id + " has to be either pending or confirmed in order to add extra quantity");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("ERROR: quantity must be > 0");
        }
        if (quantity > availableSdraio) {
            throw new IllegalArgumentException("ERROR: quantity must be <= available quantity");
        }

        extraSdraio += quantity;
        if (status == BookingStatus.CONFIRMED) {
            status = BookingStatus.PENDING;
        }
    }

    //aggiungi extra lettini con controlli
    public void addExtraLettini(int quantity, int availableLettini) {
        if (status == BookingStatus.REJECTED || status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("ERROR: booking " + id + " has to be either pending or confirmed in order to add extra quantity");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("ERROR: quantity must be > 0");
        }
        if (quantity > availableLettini) {
            throw new IllegalArgumentException("ERROR: quantity must be <= available quantity");
        }

        extraLettini += quantity;
        if (status == BookingStatus.CONFIRMED) {
            status = BookingStatus.PENDING;
        }
    }

    //aggiungi extra sedie con controlli
    public void addExtraSedie(int quantity, int availableSedie) {
        if (status == BookingStatus.REJECTED || status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("ERROR: booking " + id + " has to be either pending or confirmed in order to add extra quantity");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("ERROR: quantity must be > 0");
        }
        if (quantity > availableSedie) {
            throw new IllegalArgumentException("ERROR: quantity must be <= available quantity");
        }

        extraSedie += quantity;
        if (status == BookingStatus.CONFIRMED) {
            status = BookingStatus.PENDING;
        }
    }

    //aggiungi camerini con controlli
    public void addCamerini(int quantity, int availableCamerini) {
        if (status == BookingStatus.REJECTED || status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("ERROR: booking " + id + " has to be either pending or confirmed in order to add extra quantity");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("ERROR: quantity must be > 0");
        }
        if (quantity > availableCamerini) {
            throw new IllegalArgumentException("ERROR: quantity must be <= available quantity");
        }

        camerini += quantity;
    }
}