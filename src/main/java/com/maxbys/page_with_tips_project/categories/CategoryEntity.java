package com.maxbys.page_with_tips_project.categories;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Getter
@Entity
@EqualsAndHashCode
public class CategoryEntity {

    @Id
    private String name;
    @OnDelete(action = OnDeleteAction.CASCADE)
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
