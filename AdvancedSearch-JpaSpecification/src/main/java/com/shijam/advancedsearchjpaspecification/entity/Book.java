package com.shijam.advancedsearchjpaspecification.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String genre;

    @ElementCollection
    @CollectionTable(name = "book_authors",joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "author")
    @Builder.Default
    private Set<String> authors=new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private BookPublisher publisher;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer publishYear;

    @Column(nullable = false)
    @Builder.Default
    private Boolean availability=true;


}
