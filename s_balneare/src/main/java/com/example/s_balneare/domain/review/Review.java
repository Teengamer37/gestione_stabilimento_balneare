package com.example.s_balneare.domain.review;

import java.time.Instant;

//TODO: da implementarla nel pattern DDD-lite

public class Review {
    private final Integer id;
    private final Integer beachId;
    private final Integer customerId;

    private final int rating;               //da 1 a 5
    private final String comment;
    private final Instant createdAt;

    public Review(Integer id, Integer beachId, Integer customerId, int rating, String comment) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("ERROR: rating must be between 1 and 5");
        if (beachId == null || customerId == null || beachId <= 0 || customerId <= 0) throw new IllegalArgumentException("ERROR: beachId and/or customerId are not valid");

        this.id = id;
        this.beachId = beachId;
        this.customerId = customerId;
        this.rating = rating;
        this.comment = comment == null ? "" : comment;
        this.createdAt = Instant.now();
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
}