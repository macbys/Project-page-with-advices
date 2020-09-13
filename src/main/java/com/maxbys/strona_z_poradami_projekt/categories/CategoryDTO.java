package com.maxbys.strona_z_poradami_projekt.categories;

import lombok.*;

@Data
@Builder
public class CategoryDTO {

    private String name;
    private CategoryDTO superiorCategory;

    public static CategoryDTO apply(CategoryEntity categoryEntity) {
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .name(categoryEntity.getName())
                .superiorCategory(categoryEntity.getSuperiorCategory() != null? CategoryDTO.apply(categoryEntity.getSuperiorCategory()) : null)
                .build();
        return categoryDTO;
    }
}
