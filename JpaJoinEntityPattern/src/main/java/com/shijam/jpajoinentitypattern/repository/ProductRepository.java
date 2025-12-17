package com.shijam.jpajoinentitypattern.repository;

import com.shijam.jpajoinentitypattern.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product,String> {

    @EntityGraph(attributePaths = {
            "productCategories",
            "productCategories.category"
    })
    Optional<Product> findBySku(String sku);
    @EntityGraph(attributePaths = {
            "productCategories",
            "productCategories.category"
    })
    Set<Product> findBySkuIn(Set<String> skus);

    @Query("""
    select p
    from Product p
    join p.productCategories pc
    join pc.category c
    where c.code = :code
    """)
    Set<Product> findByCategoryCode(@Param("code") String code);

}
