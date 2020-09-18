package com.maxbys.page_with_tips_project.categories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
class CategoriesRepositoryTest {

    @Autowired
    CategoriesRepository categoriesRepository;

    @BeforeEach
    public void fillUpDatabase() throws IOException {
        File dataJson = Paths.get("src", "test", "resources", "categories.json").toFile();
        CategoryEntity[] categories = new ObjectMapper().readValue(dataJson, CategoryEntity[].class);
        Arrays.stream(categories).forEach(categoriesRepository::save);
    }

    @Test
    @DisplayName("Category not found with non-existing name")
    public void testCategoryNotFoundWithNonExistingName() {
        //Given
        //When
        Optional<CategoryEntity> categoryEntityOptional = categoriesRepository.findById("non-existent");
        //Then
        assertThat(categoryEntityOptional.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Category found with existing name")
    public void testCategoryFoundWithExistingName() {
        //Given
        //When
        Optional<CategoryEntity> categoryEntityOptional = categoriesRepository.findById("testName1");
        //Then
        assertThat(categoryEntityOptional.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Get all categories ordered by name")
    public void testGetAllCategoriesOrderedByName() {
        //Given
        List<CategoryEntity> expectedCategories = categoriesRepository.findAll();
        Collections.sort(expectedCategories,
                Comparator.comparing(CategoryEntity::getName));
        //When
        List<CategoryEntity> categoriesOrderedByName = categoriesRepository.findAllByOrderByName();
        //Then
        assertThat(categoriesOrderedByName).isEqualTo(expectedCategories);
    }

    @Test
    @DisplayName("Adding new category without superior category")
    public void testAddingNewCategoryWithoutSuperiorCategory() {
        //Given
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .name("testedCategory")
                .superiorCategory(null)
                .build();
        CategoryEntity expectedCategoryEntity = CategoryEntity.apply(categoryDTO, null);
        //When
        CategoryEntity savedCategoryEntity = categoriesRepository.save(expectedCategoryEntity);
        //Then
        assertThat(savedCategoryEntity).isEqualTo(expectedCategoryEntity);
    }

    @Test
    @DisplayName("Adding new category with superior category")
    public void testAddingNewCategoryWithSuperiorCategory() {
        //Given
        Optional<CategoryEntity> superiorCategoryOptional = categoriesRepository.findById("testName2");
        CategoryEntity superiorCategory = superiorCategoryOptional.get();
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .name("testedCategory")
                .superiorCategory(null)
                .build();
        CategoryEntity expectedCategoryEntity = CategoryEntity.apply(categoryDTO, superiorCategory);
        //When
        CategoryEntity savedCategoryEntity = categoriesRepository.save(expectedCategoryEntity);
        //Then
        assertThat(savedCategoryEntity).isEqualTo(expectedCategoryEntity);
    }

    @Test
    @DisplayName("Find all category without superior category")
    public void testFindAllCategoryWithoutSuperiorCategory() {
        //Given
        Optional<CategoryEntity> categoryOptional = categoriesRepository.findById("testName2");
        CategoryEntity categoryEntity = categoryOptional.get();
        List<CategoryEntity> expectedCategoriesList = List.of(categoryEntity);
        //When
        Page<CategoryEntity> foundCategories = categoriesRepository
                .findAllBySuperiorCategoryIsNull(PageRequest.of(0, 5));
        List<CategoryEntity> foundCategoriesList = foundCategories.getContent();
        //Then
        assertThat(foundCategoriesList).isEqualTo(expectedCategoriesList);
    }

    @Test
    @DisplayName("Don't find sub-categories of non-existing category")
    public void testThrowErrorTryingToFindAllSubCategoriesOfNonExistingCategory() {
        //Given
        //When
        Page<CategoryEntity> foundSubCategories = categoriesRepository
                .findAllBySuperiorCategoryNameIs("non-existing", PageRequest.of(0, 5));
        //Then
        assertThat(foundSubCategories.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Find all sub-categories of existing category")
    public void testFindAllSubCategoriesOfExistingCategory() {
        //Given
        Optional<CategoryEntity> categoryOptional = categoriesRepository.findById("testName1");
        CategoryEntity categoryEntity = categoryOptional.get();
        List<CategoryEntity> expectedSubCategoriesList = List.of(categoryEntity);
        //When
        Page<CategoryEntity> foundSubCategories = categoriesRepository
                .findAllBySuperiorCategoryNameIs("testName2", PageRequest.of(0, 5));
        List<CategoryEntity> foundSubCategoriesList = foundSubCategories.getContent();
        //Then
        assertThat(foundSubCategoriesList).isEqualTo(expectedSubCategoriesList);
    }
}
