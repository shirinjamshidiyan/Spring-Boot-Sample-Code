package com.shijam.jpajoinentitypattern.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryId implements Serializable {

    private String productId;
    private String categoryId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductCategoryId that))
            return false;
        return Objects.equals(productId, that.productId)
                &&
                Objects.equals(categoryId, that.categoryId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(productId, categoryId);
    }

}
