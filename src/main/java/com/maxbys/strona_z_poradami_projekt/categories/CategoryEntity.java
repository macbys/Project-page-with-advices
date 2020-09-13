package com.maxbys.strona_z_poradami_projekt.categories;

import lombok.*;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Getter
@Entity
@EqualsAndHashCode
public class CategoryEntity {

    @Id
    private String name;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne
    private CategoryEntity superiorCategory;

    public static CategoryEntity apply(CategoryDTO categoryDTO, CategoryEntity superiorCategory) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.name = categoryDTO.getName();
        categoryEntity.superiorCategory = superiorCategory;
        return categoryEntity;
    }
}
