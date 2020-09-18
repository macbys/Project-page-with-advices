package com.maxbys.page_with_tips_project.categories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriesService {

    private final CategoriesRepository categoriesRepository;

    @Autowired
    public CategoriesService(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    public List<CategoryDTO> findAllByOrOrderByName(){
        List<CategoryEntity> categoryEntities = categoriesRepository.findAllByOrderByName();
        List<CategoryDTO> categoryDTOS = categoryEntities.stream()
                .map(ce -> CategoryDTO.apply(ce))
                .collect(Collectors.toList());
        return categoryDTOS;
    }

    public CategoryDTO findById(String categoryName){
        Optional<CategoryEntity> categoryEntityOptional = categoriesRepository.findById(categoryName);
        CategoryEntity categoryEntity = categoryEntityOptional.orElseThrow(() ->
                new RuntimeException("Category with name " + categoryName + " doesn't exist"));
        CategoryDTO categoryDTO = CategoryDTO.apply(categoryEntity);
        return categoryDTO;
    }

    public void save(CategoryDTO categoryDTO){
        CategoryEntity superiorCategory;
        CategoryDTO superiorCategoryDTO = categoryDTO.getSuperiorCategory();
        if(superiorCategoryDTO == null) {
            superiorCategory = null;
        } else {
            String superiorCategoryName = superiorCategoryDTO.getName();
            Optional<CategoryEntity> superiorCategoryOptional = categoriesRepository.findById(superiorCategoryName);
            superiorCategory = superiorCategoryOptional.orElseThrow(() ->
                    new RuntimeException("Category with name " + superiorCategoryName + " doesn't exist"));
        }
        CategoryEntity categoryEntity = CategoryEntity.apply(categoryDTO, superiorCategory);
        categoriesRepository.save(categoryEntity);
    }

    public void deleteById(String id){
        categoriesRepository.deleteById(id);
    }

    public Page<CategoryDTO> findAllBySuperiorCategoryIsNull(Pageable pageable) {
        Page<CategoryEntity> allByCategoryIsNull = categoriesRepository.findAllBySuperiorCategoryIsNull(pageable);
        return getCategoryDTOS(pageable, allByCategoryIsNull);
    }

    private Page<CategoryDTO> getCategoryDTOS(Pageable pageable, Page<CategoryEntity> allByCategoryIsNull) {
        List<CategoryDTO> categoryDTOList = allByCategoryIsNull.stream()
                .map(ce -> CategoryDTO.apply(ce))
                .collect(Collectors.toList());
        PageImpl<CategoryDTO> categoryDTOS = new PageImpl<>(categoryDTOList, pageable, allByCategoryIsNull.getTotalElements());
        return categoryDTOS;
    }

    public Page<CategoryDTO> findAllBySuperiorCategoryNameIs(String id, Pageable pageable) {
        Page<CategoryEntity> allByCategoryIsNull = categoriesRepository.findAllBySuperiorCategoryNameIs(id, pageable);
        return getCategoryDTOS(pageable, allByCategoryIsNull);
    }
}
