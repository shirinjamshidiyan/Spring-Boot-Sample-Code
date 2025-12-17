package com.shijam.jpajoinentitypattern.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name= "products")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE products SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@Builder
//domain owner
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, length = 64)
    private String sku; // Stock Keeping Unit (Business key)

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 1024)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private Set<ProductCategory> productCategories = new HashSet<>();

    public void addCategory(Category category)
    {
        ProductCategory pc = new ProductCategory(this, category);
        productCategories.add(pc);
        category.getProductCategories().add(pc);
    }
    public boolean removeCategory(Category category) {
        boolean removed = productCategories.removeIf(pc -> {
            if(pc.getCategory().equals(category))
            {
                category.getProductCategories().remove(pc);
                return true; // orphanRemoval handles delete
            }
            return false;
        });
        return removed;
    }

    public Set<Category> getCategories() {
        return productCategories.stream()
                .map(ProductCategory::getCategory)
                .collect(Collectors.toSet());
    }

}
