package com.maxbys.page_with_tips_project.categories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
class CategoriesServiceTest {

    private CategoriesService categoriesService;
    @Mock
    private CategoriesRepository categoriesRepository;

    @BeforeEach
    public void setUp() {
        categoriesService = new CategoriesService(categoriesRepository);
    }

    @Test
    @DisplayName("Find categories sorted by name")
    public void testFindCategoriesSortedByName() {
        //Given
        CategoryDTO categoryDTO1 = CategoryDTO.builder()
                .name("testCategory1")
                .superiorCategory(null)
                .build();
        CategoryDTO categoryDTO2 = CategoryDTO.builder()
                .name("testCategory2")
                .superiorCategory(null)
                .build();
        List<CategoryDTO> expectedList = List.of(categoryDTO1, categoryDTO2);
        List<CategoryEntity> categoryEntities = List.of(CategoryEntity.apply(categoryDTO1, null), CategoryEntity.apply(categoryDTO2, null));
        when(categoriesRepository.findAllByOrderByName()).thenReturn(categoryEntities);
        //When
        List<CategoryDTO> foundCategoryList = categoriesService.findAllByOrOrderByName();
        //Then
        assertThat(foundCategoryList).isEqualTo(expectedList);
    }

    @Test
    @DisplayName("Throw error while trying to find non-existing category by name")
    public void testThrowErrorWhileTryingToFindNonExistingCategoryByName() {
        //Given
        String categoryName = "non-existing";
        when(categoriesRepository.findById("non-existing")).thenReturn(Optional.empty());
        //When
        //Then
        assertThatThrownBy(() -> categoriesService.findById(categoryName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category with name non-existing doesn't exist");
    }

    @Test
    @DisplayName("Find existing category")
    public void testFindExistingCategory() {
        //Given
        CategoryDTO expectedCategoryDTO = CategoryDTO.builder()
                .name("existing")
                .superiorCategory(null)
                .build();
        CategoryEntity categoryEntity = CategoryEntity.apply(expectedCategoryDTO, null);
        when(categoriesRepository.findById("existing")).thenReturn(Optional.of(categoryEntity));
        //When
        CategoryDTO foundCategoryDTO = categoriesService.findById("existing");
        //Then
        assertThat(foundCategoryDTO).isEqualTo(expectedCategoryDTO);
    }

    @Test
    @DisplayName("Enter category repository save method while adding new category")
    public void testAddingNewCategory() {
        //Given
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .name("test")
                .superiorCategory(null)
                .build();
        CategoryEntity categoryEntity = CategoryEntity.apply(categoryDTO, null);
        //When
        categoriesService.save(categoryDTO);
        //Then
        verify(categoriesRepository, times(1)).save(categoryEntity);
    }

    @Test
    @DisplayName("Find all categories without superior category")
    public void testFindAllCategoriesWithoutSuperiorCategory() {
        //Given
        CategoryDTO categoryDTO1 = CategoryDTO.builder()
                .name("testCategory1")
                .superiorCategory(null)
                .build();
        CategoryDTO categoryDTO2 = CategoryDTO.builder()
                .name("testCategory2")
                .superiorCategory(null)
                .build();
        List<CategoryDTO> expectedList = List.of(categoryDTO1, categoryDTO2);
        List<CategoryEntity> categoryEntities = List.of(CategoryEntity.apply(categoryDTO1, null), CategoryEntity.apply(categoryDTO2, null));
        PageImpl<CategoryEntity> categoryPages = new PageImpl<>(categoryEntities, PageRequest.of(0, 5), categoryEntities.size());
        PageImpl<CategoryDTO> expectedCategoriesPage = new PageImpl<>(expectedList, PageRequest.of(0, 5), expectedList.size());
        when(categoriesRepository.findAllBySuperiorCategoryIsNull(PageRequest.of(0, 5))).thenReturn(categoryPages);
        //When
        Page<CategoryDTO> foundCategories = categoriesService.findAllBySuperiorCategoryIsNull(PageRequest.of(0, 5));
        //Then
        assertThat(foundCategories).isEqualTo(expectedCategoriesPage);
    }

    @Test
    @DisplayName("Find all categories with specified superior category")
    public void testFindAllCategoriesWithSpecifiedSuperiorCategory() {
        //Given
        CategoryDTO categoryDTO1 = CategoryDTO.builder()
                .name("testCategory1")
                .superiorCategory(null)
                .build();
        CategoryDTO categoryDTO2 = CategoryDTO.builder()
                .name("testCategory2")
                .superiorCategory(categoryDTO1)
                .build();
        List<CategoryDTO> expectedList = List.of(categoryDTO2);
        CategoryEntity categoryEntity1 = CategoryEntity.apply(categoryDTO1, null);
        List<CategoryEntity> categoryEntities = List.of(CategoryEntity.apply(categoryDTO2, categoryEntity1));
        PageImpl<CategoryEntity> categoryPages = new PageImpl<>(categoryEntities, PageRequest.of(0, 5), categoryEntities.size());
        PageImpl<CategoryDTO> expectedCategoriesPage = new PageImpl<>(expectedList, PageRequest.of(0, 5), expectedList.size());
        when(categoriesRepository.findAllBySuperiorCategoryNameIs("testCategory1", PageRequest.of(0, 5))).thenReturn(categoryPages);
        //When
        Page<CategoryDTO> foundCategories = categoriesService.findAllBySuperiorCategoryNameIs("testCategory1", PageRequest.of(0, 5));
        //Then
        assertThat(foundCategories).isEqualTo(expectedCategoriesPage);
    }

}
