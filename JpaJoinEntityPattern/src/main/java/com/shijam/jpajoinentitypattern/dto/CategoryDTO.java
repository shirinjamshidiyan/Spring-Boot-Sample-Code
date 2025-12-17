package com.shijam.jpajoinentitypattern.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 64 )
    private String code; // e.g. "ELECTRONICS", "CLOTHES"
}
