package com.shijam.jpajoinentitypattern.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductDTO {

    @Size(max = 255)
    private String name;

    @Size(max = 1024)
    private String description;

    @DecimalMin(value = "0.00", inclusive = false)
    private BigDecimal price;

    private Boolean active;

    /**
     * null -> do not change categories
     * empty -> NOT allowed (each product must have at least one category)
     * otherwise -> sync categories
     */
    @Size(max = 50, message = "Maximum 50 categories allowed")
    private Set<@NotBlank String> categoryCodes;
}
