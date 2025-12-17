package com.shijam.jpajoinentitypattern.service;

import com.shijam.jpajoinentitypattern.dto.ProductDTO;
import com.shijam.jpajoinentitypattern.dto.UpdateProductDTO;
import com.shijam.jpajoinentitypattern.entity.Category;
import com.shijam.jpajoinentitypattern.entity.Product;
import com.shijam.jpajoinentitypattern.entity.ProductCategory;
import com.shijam.jpajoinentitypattern.error.BusinessException;
import com.shijam.jpajoinentitypattern.error.ErrorCode;
import com.shijam.jpajoinentitypattern.mapper.ProductMapper;
import com.shijam.jpajoinentitypattern.repository.CategoryRepository;
import com.shijam.jpajoinentitypattern.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;


    @Transactional
    public ProductDTO createProduct(ProductDTO dto)
    {
        normalize(dto);
        if(productRepository.findBySku(dto.getSku()).isPresent())
            throw new BusinessException(
                    "sku_already_exists for code : "+ dto.getSku(),
                    ErrorCode.CONFLICT);

        Product entity = productMapper.toEntity(dto);

        Set<Category> categories = resolveCategories(dto.getCategoryCodes());
        categories.forEach(entity::addCategory);

        Product save = productRepository.save(entity);
        return productMapper.toDto(save);
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductBySku(String sku) {

        return productRepository.
                findBySku(sku.trim()).
                map(productMapper::toDto).
                orElseThrow(() -> new BusinessException(
                        "product_not_found for sku :" + sku,
                        ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Set<ProductDTO> getProductListBySku(List<String> skus) {

        Set<String> distinctSkus = skus.stream().map(String::trim)
                .collect(Collectors.toSet());

        if (distinctSkus.isEmpty())
            throw new BusinessException("empty_sku_list", ErrorCode.VALIDATION_ERROR);

        Set<Product> products = productRepository.findBySkuIn(distinctSkus);

        if (products.size() != distinctSkus.size()) {
            Set<String> foundSkus = products.stream()
                    .map(Product::getSku)
                    .collect(Collectors.toSet());
            Set<String> missing = distinctSkus.stream()
                    .filter(s -> !foundSkus.contains(s))
                    .collect(Collectors.toSet());
            throw new BusinessException("unavailable skus : "+ missing ,
                    ErrorCode.PRODUCT_NOT_FOUND);
        }

        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Transactional
    public ProductDTO updateBySku(String sku, UpdateProductDTO dto)
    {
        normalize(dto);
        Product product = productRepository.findBySku(sku.trim())
                .orElseThrow(() ->
                        new BusinessException(
                                "product_not_found for sku :" + sku.trim(),
                                ErrorCode.PRODUCT_NOT_FOUND));

        productMapper.updateEntity(dto,product);

        // sync Set<ProductCategory> productCategories if provided
        if (dto.getCategoryCodes() != null) {

            if (dto.getCategoryCodes().isEmpty()) {
                throw new BusinessException(
                        "product_must_have_at_least_one_category",
                        ErrorCode.VALIDATION_ERROR
                );
            }
            syncCategories(product, dto.getCategoryCodes());
        }
        Product saved = productRepository.save(product);
        return productMapper.toDto(saved);
    }

    @Transactional
    public void deleteBySku(String sku)
    {
        Product product = productRepository.findBySku(sku.trim())
                .orElseThrow(() -> new BusinessException(
                        "product_not_found for sku : " + sku.trim(),
                         ErrorCode.PRODUCT_NOT_FOUND));
        productRepository.delete(product);   // triggers @SQLDelete
    }

    @Transactional
    public void activate(String sku) {

        Product product = productRepository.findBySku(sku.trim())
                .orElseThrow(() -> new BusinessException(
                        "product_not_found for sku: " + sku.trim(),
                        ErrorCode.PRODUCT_NOT_FOUND
                ));

        if (!product.isActive()) {
            product.setActive(true);
        }
    }

    @Transactional
    public void deactivate(String sku) {

        Product product = productRepository.findBySku(sku.trim())
                .orElseThrow(() -> new BusinessException(
                        "product_not_found for sku: " + sku,
                        ErrorCode.PRODUCT_NOT_FOUND
                ));

        if (product.isActive()) {
            product.setActive(false);
        }
    }

    private Set<Category> resolveCategories(Set<String> catCodes) {

        if (catCodes == null || catCodes.isEmpty()) {
            throw new BusinessException("category_must_be_specified",
                    ErrorCode.VALIDATION_ERROR);
        }
        Set<Category> found = categoryRepository.findByCodeIn(catCodes);

        if (found.size() != catCodes.size()) {
            Set<String> foundCodes = found.stream()
                    .map(Category::getCode)
                    .collect(Collectors.toSet());
            Set<String> missing = catCodes.stream().
                    filter(c -> !foundCodes.contains(c))
                    .collect(Collectors.toSet());

            throw new BusinessException("category_not_found for : " + missing,
                    ErrorCode.CATEGORY_NOT_FOUND);
        }
        return found;
    }

    private void syncCategories(Product product, Set<String> codesRaw) {

        //update categories we want to assign
        Set<Category> target = resolveCategories(codesRaw);
        //current categories we want to update
        Set<Category> current = product.getProductCategories()
                .stream()
                .map(ProductCategory::getCategory)
                .collect(Collectors.toSet());

        // remove categories not in target
        current.stream().filter(c -> !target.contains(c)).forEach(product::removeCategory);
        //add categories not already exist in current
        target.stream().filter(c -> !current.contains(c)).forEach(product::addCategory);
    }

    private void normalize(ProductDTO dto) {
        if (dto.getSku() != null) {
            dto.setSku(dto.getSku().trim());
        }
        if (dto.getName() != null) {
            dto.setName(dto.getName().trim());
        }
        if (dto.getDescription() != null) {
            dto.setDescription(dto.getDescription().trim());
        }
        if (dto.getCategoryCodes() != null) {
            dto.setCategoryCodes(
                    dto.getCategoryCodes().stream()
                            .map(String::trim)
                            .collect(Collectors.toSet())
            );
        }
    }

    private void normalize(UpdateProductDTO dto) {
        if (dto.getName() != null) {
            dto.setName(dto.getName().trim());
        }
        if (dto.getDescription() != null) {
            dto.setDescription(dto.getDescription().trim());
        }
        if (dto.getCategoryCodes() != null) {
            dto.setCategoryCodes(
                    dto.getCategoryCodes().stream()
                            .map(String::trim)
                            .collect(Collectors.toSet())
            );
        }
    }

    //--------------- ProductCategoryController metods -------------
    @Transactional
    public void removeCategoryFromProduct(String sku, String catCode)
    {
        Product product = productRepository.findBySku(sku.trim())
                .orElseThrow(() -> new BusinessException(
                        "product_not_found for sku: " + sku.trim(),
                        ErrorCode.PRODUCT_NOT_FOUND));

        String code = catCode.trim();
        Category category = categoryRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(
                        "category_not_found for code: " + code,
                        ErrorCode.CATEGORY_NOT_FOUND));

        boolean removed = product.removeCategory(category);
        if (!removed) {
            throw new BusinessException(
                    "category_not_assigned_to_product",
                    ErrorCode.VALIDATION_ERROR
            );
        }

        //because our invariant here is that: each product must have at least one category
        if (product.getProductCategories().isEmpty()) {
            throw new BusinessException(
                    "product_must_have_at_least_one_category",
                    ErrorCode.VALIDATION_ERROR
            );
        }
    }

    @Transactional
    public void addCategoryToProduct(String sku, String catCode) {
        Product product = productRepository.findBySku(sku.trim())
                .orElseThrow(() -> new BusinessException(
                        "product_not_found for sku : " + sku.trim(),
                        ErrorCode.PRODUCT_NOT_FOUND));

        String code = catCode.trim();
        Category category = categoryRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(
                        "category_not_found for code: " + code,
                        ErrorCode.CATEGORY_NOT_FOUND));
        product.addCategory(category); // Set + equals/hashCode prevent adding duplicate category
    }

    @Transactional
    public void replaceCategories(String sku, Set<String> categoryCodes) {

        Product product = productRepository.findBySku(sku.trim())
                .orElseThrow(() -> new BusinessException(
                        "product_not_found for sku : " + sku.trim(),
                        ErrorCode.PRODUCT_NOT_FOUND));

        Set<String> codes = categoryCodes.stream()
                .map(String::trim)
                .collect(Collectors.toSet());

        if (codes == null || codes.isEmpty()) {
            throw new BusinessException(
                    "product_must_have_at_least_one_category",
                    ErrorCode.VALIDATION_ERROR
            );
        }
        syncCategories(product, codes);
    }


}
