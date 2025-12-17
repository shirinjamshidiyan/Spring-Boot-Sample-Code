package com.shijam.jpajoinentitypattern.service;

import com.shijam.jpajoinentitypattern.dto.CategoryDTO;
import com.shijam.jpajoinentitypattern.dto.ProductDTO;
import com.shijam.jpajoinentitypattern.entity.Category;
import com.shijam.jpajoinentitypattern.entity.ProductCategory;
import com.shijam.jpajoinentitypattern.error.BusinessException;
import com.shijam.jpajoinentitypattern.error.ErrorCode;
import com.shijam.jpajoinentitypattern.mapper.ProductMapper;
import com.shijam.jpajoinentitypattern.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    @Transactional
    public CategoryDTO create(CategoryDTO dto) {
        normalize(dto);
        if (categoryRepository.existsByCode(dto.getCode())) {
            throw new BusinessException(
                    "category_code_already_exists for code : "+ dto.getCode(),
                    ErrorCode.CONFLICT
            );
        }
        Category category = Category.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .build();
        Category saved = categoryRepository.save(category);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Set<CategoryDTO> getAll()
    {
        return categoryRepository
                .findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public CategoryDTO getByCode(String code)
    {
        return categoryRepository
                .findByCode(code.trim())
                .map(this::toDto)
                .orElseThrow(() -> new BusinessException
                        ("category_not_found for code : " + code,
                                ErrorCode.CATEGORY_NOT_FOUND));
    }

    private CategoryDTO toDto(Category category) {
        return new CategoryDTO(category.getName(), category.getCode());
    }
    private void normalize(CategoryDTO dto) {
        if (dto.getName() != null) {
            dto.setName(dto.getName().trim());
        }
        if (dto.getCode() != null) {
            dto.setCode(dto.getCode().trim());
        }
    }

    @Transactional(readOnly = true)
    public Set<ProductDTO> getProductsByCategory(String code) {

        Category category = categoryRepository.findByCode(code.trim())
                .orElseThrow(() -> new BusinessException(
                        "category_not_found",
                        ErrorCode.CATEGORY_NOT_FOUND));

        return category.getProducts().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toSet());
    }

}
