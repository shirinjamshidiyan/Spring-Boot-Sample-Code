package com.shijam.jpajoinentitypattern.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    @NotBlank
    @Size(max = 64)
    private String sku; // Stock Keeping Unit (Business key)

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 1024)
    private String description;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = false)
    private BigDecimal price;

    /*  optional, default handled in entity */
    private Boolean active;  //important :Boolean not boolean

    @NotEmpty
    @Size(max = 50, message = "Maximum 50 categories allowed")
    private Set<@NotBlank String> categoryCodes;

}
