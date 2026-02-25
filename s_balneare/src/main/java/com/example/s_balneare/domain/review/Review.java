package com.example.s_balneare.domain.review;

import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.user.CustomerUser;

import java.time.Instant;

public class Review {
    private final int id;
    private final int beachId;
    private final int customerId;

    private final int rating;               //da 1 a 5
    private final String comment;
    private final Instant createdAt;

    public Review(int id, int beachId, int customerId, int rating, String comment) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("ERROR: rating must be between 1 and 5");
        if (beachId <= 0 || customerId <= 0) throw new IllegalArgumentException("ERROR: beachId and customerId are not valid");

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