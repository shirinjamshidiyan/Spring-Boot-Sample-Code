package com.shijam.advancedsearchjpaspecification.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDTO {
    private String title;
    private String genre;
    private Set<String> authors;
    private String publisherName;
    private BigDecimal price;
    private Boolean availability;
    private Integer publishYear;
}
