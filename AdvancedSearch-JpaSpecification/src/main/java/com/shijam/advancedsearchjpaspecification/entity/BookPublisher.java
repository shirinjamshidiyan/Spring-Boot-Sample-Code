package com.shijam.advancedsearchjpaspecification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book_publishers")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookPublisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "publisher",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH,CascadeType.REFRESH} ,
            orphanRemoval = true)
    @Builder.Default
    private List<Book> books=new ArrayList<>();

}
