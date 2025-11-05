package com.shijam.advancedsearchjpaspecification.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookSearchCriteria {

    @Size(max = 100, message = "Title should be at most 100 characters")
    private String title;  // partial match
    private String genre;  // exact match
    @Size(max = 50, message = "Author name should be at most 50 characters")
    private String author; // partial match
    private String publisher;
    @Min(value = 0, message = "Min price must be >= 0")
    private BigDecimal minPrice;
    @Min(value = 0, message = "Max price must be >= 0")
    private BigDecimal maxPrice;
    private Boolean availability;
    @Min(value = 0, message = "Publish Year must be >= 0")
    private Integer publishYear;
    @Builder.Default
    private String operator = "AND";

}
