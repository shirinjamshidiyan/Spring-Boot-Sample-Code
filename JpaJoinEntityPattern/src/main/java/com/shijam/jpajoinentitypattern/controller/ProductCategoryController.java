package com.shijam.jpajoinentitypattern.controller;

import com.shijam.jpajoinentitypattern.service.ProductService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/api/products/{sku}/categories")
@AllArgsConstructor
@Validated
public class ProductCategoryController {

    private final ProductService productService;

    /**
     * Replace categories of a product (full sync)
     * null body  -> not allowed
     * empty set  -> not allowed (because each product belongs to at least one category
     * non-empty  -> sync to final state
     */
    @PutMapping
    public ResponseEntity<Void> replaceCategories(
            @PathVariable(name="sku") @NotBlank String sku,
            @RequestBody @NotEmpty @Size(max = 50) Set<@NotBlank String> categoryCodes) {

        productService.replaceCategories(sku, categoryCodes);
        return ResponseEntity.noContent().build();
    }

     // Add a single category to product
    @PostMapping("/{code}")
    public ResponseEntity<Void> addCategory(
            @PathVariable(name = "sku") @NotBlank String sku,
            @PathVariable(name="code") @NotBlank String code) {

        productService.addCategoryToProduct(sku, code);
        return ResponseEntity.noContent().build();
    }

     // Remove a single category from product
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> removeCategory(
            @PathVariable(name = "sku") @NotBlank String sku,
            @PathVariable(name="code") @NotBlank String code) {

        productService.removeCategoryFromProduct(sku, code);
        return ResponseEntity.noContent().build();
    }


}
