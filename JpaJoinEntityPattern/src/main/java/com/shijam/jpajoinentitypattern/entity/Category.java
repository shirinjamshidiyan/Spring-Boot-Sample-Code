package com.shijam.jpajoinentitypattern.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "categories")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 64)
    private String code; // e.g. "ELECTRONICS", "CLOTHES"

    @OneToMany(mappedBy = "category")
    @Builder.Default
    private Set<ProductCategory> productCategories = new HashSet<>();

    //Category doesnt need addProduct() and RemoveProduct()
    //It's Passive and Product is domain owner and the owner of relationship

    // READ ONLY
    public Set<Product> getProducts() {
        return productCategories.stream()
                .map(ProductCategory::getProduct)
                .collect(Collectors.toSet());
    }
}
