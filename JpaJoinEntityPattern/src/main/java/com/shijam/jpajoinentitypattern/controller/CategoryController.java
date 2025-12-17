package com.shijam.jpajoinentitypattern.controller;

import com.shijam.jpajoinentitypattern.dto.CategoryDTO;
import com.shijam.jpajoinentitypattern.dto.ProductDTO;
import com.shijam.jpajoinentitypattern.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/api/categories")
@AllArgsConstructor
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> create(@RequestBody @Valid CategoryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.create(dto));
    }

    @GetMapping
    public ResponseEntity<Set<CategoryDTO>> listCategories() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/{code}")
    public ResponseEntity<CategoryDTO> getByCode(
            @PathVariable(name = "code") @NotBlank String code) {
        return ResponseEntity.ok(categoryService.getByCode(code));
    }
    @GetMapping("/{code}/products")
    public ResponseEntity<Set<ProductDTO>> getProducts(
            @PathVariable(name = "code") @NotBlank String code
    ) {
        return ResponseEntity.ok(categoryService.getProductsByCategory(code));
    }

   
}
