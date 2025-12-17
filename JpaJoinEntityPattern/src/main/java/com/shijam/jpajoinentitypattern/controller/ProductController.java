package com.shijam.jpajoinentitypattern.controller;

import com.shijam.jpajoinentitypattern.dto.ProductDTO;
import com.shijam.jpajoinentitypattern.dto.UpdateProductDTO;
import com.shijam.jpajoinentitypattern.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDTO> create(@RequestBody @Valid ProductDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.createProduct(dto));
    }

    //This endpoint supports partial updates.
    //Only fields provided in the request body will be updated.
    //Omitted or null fields remain unchanged.
    @PatchMapping("/{sku}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable(name = "sku") @NotBlank String sku,
            @RequestBody @Valid UpdateProductDTO dto) {

        return ResponseEntity.ok(productService.updateBySku(sku, dto)); // 200
    }

    @GetMapping("/{sku}")
    public ResponseEntity<ProductDTO> getProductBySku(
            @PathVariable("sku") @NotBlank String sku){
        return ResponseEntity.ok(productService.getProductBySku(sku));
    }

    @GetMapping("/by-skus")
    public ResponseEntity<Set<ProductDTO>> geProductsBySkus(
            @RequestParam(name = "skus") @Size(min = 1, max = 100)  List<@NotBlank String> skus){

        return ResponseEntity.ok(productService.getProductListBySku(skus));
    }

    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> deleteBySku(
            @PathVariable(name = "sku") @NotBlank String sku) {
        productService.deleteBySku(sku);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{sku}/activate")
    public void activate(@PathVariable(name = "sku") @NotBlank String sku) {
        productService.activate(sku);
    }

    @PostMapping("/{sku}/deactivate")
    public void deactivate(@PathVariable(name = "sku") @NotBlank String sku) {
        productService.deactivate(sku);
    }

}
