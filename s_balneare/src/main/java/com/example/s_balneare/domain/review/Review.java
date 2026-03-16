package com.example.s_balneare.domain.review;

import java.time.Instant;

/// Rappresenta una recensione lasciata da un cliente per una specifica spiaggia
public class Review {
    //dati attori review
    private final Integer id;
    private final Integer beachId;
    private final Integer customerId;

    //dati review
    private final int rating;               //da 1 a 5
    private final String comment;
    private final Instant createdAt;

    //costruttore completo per assicurarsi l'integrità dei valori
    public Review(Integer id, Integer beachId, Integer customerId, int rating, String comment, Instant createdAt) {
        checkBeachId(beachId);
        checkCustomerId(customerId);
        checkRating(rating);
        checkCreatedAt(createdAt);

        this.id = id;
        this.beachId = beachId;
        this.customerId = customerId;
        this.rating = rating;
        this.comment = checkComment(comment);
        this.createdAt = createdAt;
    }

    //getters
    public Integer getId() {
        return id;
    }
    public Integer getBeachId() {
        return beachId;
    }
    public Integer getCustomerId() {
        return customerId;
    }
    public int getRating() {
        return rating;
    }
    public String getComment() {
        return comment;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }

    //---- METODI CHECKERS ----
    private void checkBeachId(Integer beachId) {
        if (beachId == null || beachId <= 0 ) throw new IllegalArgumentException("ERROR: beachId is not valid");
    }
    private void checkCustomerId(Integer customerId) {
        if (customerId == null || customerId <= 0 ) throw new IllegalArgumentException("ERROR: customerId is not valid");
    }
    private void checkRating(int rating) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("ERROR: rating must be between 1 and 5");
    }
    private void checkCreatedAt(Instant createdAt) {
        if (createdAt == null) throw new IllegalArgumentException("ERROR: createdAt must not be null");
    }
    private String checkComment(String comment) {
        if (comment == null || comment.isBlank()) throw new IllegalArgumentException("ERROR: comment must not be null/blank");
        if (comment.length() > 1024) throw new IllegalArgumentException("ERROR: comment cannot exceed 1024 characters");
        return comment.trim();
    }
}