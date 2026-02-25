package com.example.s_balneare.domain.booking;

import java.time.LocalDate;
import java.util.List;

public class Booking {
    //dati booking
    private final int id;
    private final int beachId;
    private final int customerId;
    private final LocalDate date;

    //oggetti booking
    private final List<Integer> spotIds;
    private int extraSdraio;
    private int extraLettini;
    private int extraSedie;
    private int camerini;

    //stato booking
    private BookingStatus status;

    //costruttore fatto con Builder Pattern
    private Booking(BookingBuilder builder) {
        //check dati final
        if (builder.beachId <= 0) throw new IllegalArgumentException("ERROR: beachId not valid for booking " + builder.id);
        if (builder.customerId <= 0) throw new IllegalArgumentException("ERROR: customerId not valid for booking " + builder.id);
        if (builder.date == null) throw new IllegalArgumentException("ERROR: date cannot be null for booking " + builder.id);
        if (builder.spotIds == null || builder.spotIds.isEmpty()) throw new IllegalArgumentException("ERROR: at least one spot must be selected for booking " + builder.id);

        //check integrità interi
        if (builder.extraSdraio < 0 || builder.extraLettini < 0 || builder.extraSedie < 0 || builder.camerini < 0) {
            throw new IllegalArgumentException("ERROR: extra quantity must be >=0 for booking " + builder.id);
        }

        this.id = builder.id;
        this.beachId = builder.beachId;
        this.customerId = builder.customerId;
        this.date = builder.date;
        this.spotIds = builder.spotIds;
        this.extraSdraio = builder.extraSdraio;
        this.extraLettini = builder.extraLettini;
        this.extraSedie = builder.extraSedie;
        this.camerini = builder.camerini;
        this.status = BookingStatus.PENDING;
    }

    //builder pattern
    public static class BookingBuilder {
        private final int id;
        private final int beachId;
        private final int customerId;
        private final LocalDate date;

        private final List<Integer> spotIds;
        private int extraSdraio = 0;
        private int extraLettini = 0;
        private int extraSedie = 0;
        private int camerini = 0;

        private BookingStatus status = BookingStatus.PENDING;

        public BookingBuilder(int id, int beachId, int customerId, LocalDate date, List<Integer> spotIds) {
            this.id = id;
            this.beachId = beachId;
            this.customerId = customerId;
            this.date = date;
            this.spotIds = spotIds;
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

        public BookingBuilder status(BookingStatus status) {
            this.status = status;
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
    public int getBeachId() {
        return beachId;
    }
    public int getCustomerId() {
        return customerId;
    }
    public LocalDate getDate() {
        return date;
    }
    public List<Integer> getSpotIds() {
        return spotIds;
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