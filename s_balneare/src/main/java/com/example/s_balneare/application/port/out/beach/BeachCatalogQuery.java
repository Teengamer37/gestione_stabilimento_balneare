package com.example.s_balneare.application.port.out.beach;

import com.example.s_balneare.domain.common.TransactionContext;

import java.util.List;

public interface BeachCatalogQuery {
    List<BeachSummary> searchActiveBeaches(String keyword, TransactionContext context);
}

/*
TODO:

// 1. Create a DTO for the detail page
public record BeachDetailPageDto(
    Beach beachData,          // You can reuse your findById method for this part
    double averageRating,     // SELECT AVG(rating) FROM reviews WHERE beachId = ?
    List<ReviewDto> latestReviews // SELECT * FROM reviews WHERE beachId = ? ORDER BY createdAt DESC LIMIT 10
) {}

// 2. Create the Query Port
public interface BeachDetailsQuery {
    BeachDetailPageDto getFullBeachDetails(Integer beachId);
}
 */