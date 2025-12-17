package com.shijam.jpajoinentitypattern.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "product_categories")
@Getter
@Setter
@NoArgsConstructor
/*
Join Entity is a first-class-entity
This code provides Product ↔ Category many-to-many relation via ProductCategory
Clean place to add more needed fields like visibility, assignedBy,...
@ManyToMany does not allow this but a join entity does
 */
public class ProductCategory {

    @EmbeddedId
    private ProductCategoryId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("productId")
    //MapsId ensures that the Product identity is derived from its associations,
    // preventing duplication and inconsistency.
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public ProductCategory(Product product, Category category) {
        // JPA set the id after persisting the parents
        this.product = product;
        this.category = category;
        this.id = new ProductCategoryId();// EmbeddedId must exist before @MapsId
    }

    // explicit equality definitions because Join entities's identity is derived from
    // associations and they are heavily manipulated inside collections.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductCategory that))
            return false;
        return Objects.equals(product, that.product)
                &&
                Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, category);
    }
}
