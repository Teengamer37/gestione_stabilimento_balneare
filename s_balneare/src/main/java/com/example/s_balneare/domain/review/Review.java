package com.example.s_balneare.domain.review;

import java.time.Instant;
import java.util.UUID;

public class Review {
    private final UUID id;
    private final UUID beachID;
    private final UUID customerID;

    private final int rating;               //da 1 a 5
    private final String comment;
    private final Instant createdAt;

    public Review(UUID id, UUID beachID, UUID customerID, int rating, String comment) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("ERROR: rating must be between 1 and 5");
        this.id = id;
        this.beachID = beachID;
        this.customerID = customerID;
        this.rating = rating;
        this.comment = comment == null ? "" : comment;
        this.createdAt = Instant.now();
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

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
