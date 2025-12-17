package com.shijam.jpajoinentitypattern.repository;
import com.shijam.jpajoinentitypattern.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category,String> {
    Set<Category> findByCodeIn(Set<String> codes);

    Optional<Category> findByCode(String code);

    boolean existsByCode(String code);
}
