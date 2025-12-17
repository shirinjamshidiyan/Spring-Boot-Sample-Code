package com.shijam.jpajoinentitypattern.mapper;

import com.shijam.jpajoinentitypattern.dto.ProductDTO;
import com.shijam.jpajoinentitypattern.dto.UpdateProductDTO;
import com.shijam.jpajoinentitypattern.entity.Product;
import com.shijam.jpajoinentitypattern.entity.ProductCategory;
import org.mapstruct.*;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProductMapper {

    //<<<<<<<<<<<<<<<<<<<<<<<<<ِDTO -> Entity (Create)>>>>>>>>>>>>>>>>>>>>>>>>>>
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "productCategories",ignore = true) // must be completed in service
    @Mapping(
            target = "active",
            expression = "java(dto.getActive() != null ? dto.getActive() : true)"
            //because we want default value to be true (if it's not specified)
    )
    Product toEntity(ProductDTO dto);

    //<<<<<<<<<<<<<<<<<<<<<<<<<ِDTO -> Entity (Update)>>>>>>>>>>>>>>>>>>>>>>>>>>
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "productCategories", ignore = true)
    void updateEntity(UpdateProductDTO dto, @MappingTarget Product product);

    //<<<<<<<<<<<<<<<<<<<<<<<<<ِ Entity -> DTO >>>>>>>>>>>>>>>>>>>>>>>>>>>
    @Mapping(source = "productCategories",target = "categoryCodes" , qualifiedByName = "productCategoriesToCodes")
    ProductDTO toDto(Product product);

    @Named("productCategoriesToCodes")
    static Set<String> mapProductCategoriesToCodes(Set<ProductCategory> pcategories)
     {
        if (pcategories == null || pcategories.isEmpty()) return Set.of();
       return pcategories
                .stream()
                .map(pc-> pc.getCategory().getCode())
                .collect(Collectors.toSet());

    }

}
