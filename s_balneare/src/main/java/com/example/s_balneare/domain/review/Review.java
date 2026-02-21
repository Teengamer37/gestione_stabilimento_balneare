package com.example.s_balneare.domain.review;

import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.user.CustomerUser;

import java.time.Instant;

public class Review {
    private final int id;
    private final Beach beach;
    private final CustomerUser customer;

    private final int rating;               //da 1 a 5
    private final String comment;
    private final Instant createdAt;

    public Review(int id, Beach beach, CustomerUser customer, int rating, String comment) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("ERROR: rating must be between 1 and 5");
        this.id = id;
        this.beach = beach;
        this.customer = customer;
        this.rating = rating;
        this.comment = comment == null ? "" : comment;
        this.createdAt = Instant.now();
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