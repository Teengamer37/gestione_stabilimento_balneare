package com.example.s_balneare.domain.review;

import java.time.Instant;

//TODO: Per il DDD lite raiting viene consigliato di farlo diventare un value object , in modo da poterlo usare in altri costrutti coi voti.
// Decidi te cosa vuoi fare io te lo segnalo per dovere di cronaca

public class Review {
    private final Integer id;
    private final Integer beachId;
    private final Integer customerId;

    private final int rating;               //da 1 a 5
    private final String comment;
    private final Instant createdAt;

    public Review(Integer id, Integer beachId, Integer customerId, int rating, String comment, Instant createdAt) {
        checkBeachId(beachId);
        checkCustomerId(customerId);
        checkRating(rating);
        this.id = id;
        this.beachId = beachId;
        this.customerId = customerId;
        this.rating = rating;
        this.comment = formatComment(comment);
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getBeachId() {
        return beachId;
    }

    public int getCustomerId() {
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

    private String formatComment(String comment) {
        if (comment == null) {
            return "";
        }
        return comment.trim(); // Esempio: in DDD è utile anche pulire gli spazi
    }

}