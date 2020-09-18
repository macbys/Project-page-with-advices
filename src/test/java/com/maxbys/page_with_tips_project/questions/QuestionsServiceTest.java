package com.maxbys.page_with_tips_project.questions;

import com.maxbys.page_with_tips_project.categories.CategoriesRepository;
import com.maxbys.page_with_tips_project.categories.CategoryDTO;
import com.maxbys.page_with_tips_project.categories.CategoryEntity;
import com.maxbys.page_with_tips_project.users.FormUserTemplateDTO;
import com.maxbys.page_with_tips_project.users.UserDTO;
import com.maxbys.page_with_tips_project.users.UserEntity;
import com.maxbys.page_with_tips_project.users.UsersRepository;
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
class QuestionsServiceTest {

    private QuestionsService questionsService;
    @Mock
    private QuestionsRepository questionsRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private CategoriesRepository categoriesRepository;

    @BeforeEach
    public void setUp() {
        questionsService = new QuestionsService(questionsRepository, usersRepository, categoriesRepository);
    }

    @Test
    @DisplayName("Find question with existing id")
    public void testFindQuestionWithExistingId() {
        //Given
        CategoryEntity categoryEntity = getCategoryEntity();
        UserEntity userEntity = getUserEntity();
        QuestionEntity questionEntity = getQuestionEntity(categoryEntity, userEntity);
        when(questionsRepository.findById(1L)).thenReturn(Optional.of(questionEntity));
        //When
        QuestionDTO foundQuestionDTO = questionsService.findById(1L);
        //Then
        assertThatFoundQuestionValueIsEqualToExpectedQuestionValue(questionEntity, foundQuestionDTO);
        assertThatFoundQuestionCategoryNameIsEqualToExpectedValue(categoryEntity, foundQuestionDTO);
        assertThatFoundQuestionUserEmailIsEqualToExpectedValue(userEntity, foundQuestionDTO);
    }

    private CategoryEntity getCategoryEntity() {
        CategoryDTO expectedCategoryDTO = CategoryDTO.builder()
                .name("testName")
                .build();
        return CategoryEntity.apply(expectedCategoryDTO, null);
    }

    private UserEntity getUserEntity() {
        FormUserTemplateDTO formUserTemplateDTO = FormUserTemplateDTO.builder()
                .name("testName")
                .email("testEmail")
                .password("testPassword")
                .build();
        return UserEntity.apply(formUserTemplateDTO);
    }

    private QuestionEntity getQuestionEntity(CategoryEntity categoryEntity, UserEntity userEntity) {
        FormQuestionTemplate formQuestionTemplate = getFormQuestionTemplate();
        return QuestionEntity.apply(formQuestionTemplate, userEntity, categoryEntity);
    }

    private FormQuestionTemplate getFormQuestionTemplate() {
        return FormQuestionTemplate.builder()
                .id(1L)
                .questionValue("testValue")
                .category("testName")
                .build();
    }

    private void assertThatFoundQuestionValueIsEqualToExpectedQuestionValue(QuestionEntity questionEntity, QuestionDTO foundQuestionDTO) {
        String expectedQuestionValue = questionEntity.getValue();
        String foundQuestionValue = foundQuestionDTO.getValue();
        assertThat(foundQuestionValue).isEqualTo(expectedQuestionValue);
    }

    private void assertThatFoundQuestionCategoryNameIsEqualToExpectedValue(CategoryEntity categoryEntity, QuestionDTO foundQuestionDTO) {
        CategoryDTO foundCategoryDTO = foundQuestionDTO.getCategoryDTO();
        String foundCategoryDTOName = foundCategoryDTO.getName();
        String expectedCategoryName = categoryEntity.getName();
        assertThat(foundCategoryDTOName).isEqualTo(expectedCategoryName);
    }

    private void assertThatFoundQuestionUserEmailIsEqualToExpectedValue(UserEntity userEntity, QuestionDTO foundQuestionDTO) {
        UserDTO foundUserDTO = foundQuestionDTO.getUserDTO();
        String foundEmail = foundUserDTO.getEmail();
        String expectedEmail = userEntity.getEmail();
        assertThat(foundEmail).isEqualTo(expectedEmail);
    }

    @Test
    @DisplayName("Throw error while trying to find question with non-existing id")
    public void testThrowErrorWhileTryingToFindQuestionWithNonExistingId() {
        //Given
        when(questionsRepository.findById(1000L)).thenReturn(Optional.empty());
        //When
        //Then
        assertThatThrownBy(() -> questionsService.findById(1000L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Question with id 1000 doesn't exist");
    }

    @Test
    @DisplayName("Enter questions repository save method while saving question")
    public void testAddingNewQuestion() {
        //Given
        CategoryEntity categoryEntity = getCategoryEntity();
        when(categoriesRepository.findById("testName")).thenReturn(Optional.of(categoryEntity));
        UserEntity userEntity = getUserEntity();
        when(usersRepository.findByEmail("testEmail")).thenReturn(Optional.of(userEntity));
        QuestionEntity questionEntity = getQuestionEntity(categoryEntity, userEntity);
        UserDTO userDTO = UserDTO.apply(userEntity);
        FormQuestionTemplate formQuestionTemplate = getFormQuestionTemplate();
        //When
        questionsService.save(formQuestionTemplate, userDTO);
        //Then
        verify(questionsRepository, times(1)).save(questionEntity);
    }

    @Test
    @DisplayName("Enter questions repository save method while editing question")
    public void testUpdateQuestion() {
        //Given
        CategoryEntity categoryEntity = getCategoryEntity();
        UserEntity userEntity = getUserEntity();
        QuestionEntity questionEntity = getQuestionEntity(categoryEntity, userEntity);
        when(questionsRepository.findById(1L)).thenReturn(Optional.of(questionEntity));
        FormQuestionTemplate editedFormQuestion = getFormQuestionTemplate();
        editedFormQuestion.setQuestionValue("editedValue");
        QuestionEntity expectedQuestion = QuestionEntity.apply(editedFormQuestion, userEntity, categoryEntity);
        Long questionId = questionEntity.getId();
        //When
        questionsService.update(editedFormQuestion, questionId);
        //Then
        verify(questionsRepository, times(1)).save(expectedQuestion);
    }

    @Test
    @DisplayName("Enter questions repository delete method")
    public void testDeleteQuestion() {
        //Given
        //When
        questionsService.deleteById(1L);
        //Then
        verify(questionsRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Find all questions with specified category")
    public void testFindAllQuestionsWithSpecifiedCategory() {
        //Given
        CategoryEntity categoryEntity = getCategoryEntity();
        UserEntity userEntity = getUserEntity();
        QuestionEntity questionEntity = getQuestionEntity(categoryEntity, userEntity);
        PageRequest pageable = PageRequest.of(0, 5);
        mockingFindAllQuestionsByCategoryName(questionEntity, pageable);
        PageImpl<QuestionDTO> expectedQuestionsPage = getExpectedQuestionPage(questionEntity, pageable);
        //When
        Page<QuestionDTO> foundQuestions = questionsService.findAllByCategoryIs("testName", pageable);
        //Then
        assertThat(foundQuestions).isEqualTo(expectedQuestionsPage);
    }

    private void mockingFindAllQuestionsByCategoryName(QuestionEntity questionEntity, PageRequest pageable) {
        List<QuestionEntity> questionEntityList = List.of(questionEntity);
        PageImpl<QuestionEntity> questionEntitiesPage = new PageImpl<>(questionEntityList, pageable, questionEntityList.size());
        when(questionsRepository.findAllByCategoryEntityNameIs("testName", pageable)).thenReturn(questionEntitiesPage);
    }

    private PageImpl<QuestionDTO> getExpectedQuestionPage(QuestionEntity questionEntity, PageRequest pageable) {
        QuestionDTO questionDTO = QuestionDTO.apply(questionEntity);
        List<QuestionDTO> questionDTOList = List.of(questionDTO);
        return new PageImpl<>(questionDTOList, pageable, questionDTOList.size());
    }

    @Test
    @DisplayName("Find all questions of specified user")
    public void testFindAllQuestionsOfSpecifiedUser() {
        //Given
        CategoryEntity categoryEntity = getCategoryEntity();
        UserEntity userEntity = getUserEntity();
        QuestionEntity questionEntity = getQuestionEntity(categoryEntity, userEntity);
        PageRequest pageable = PageRequest.of(0, 5);
        mockingFindAllQuestionsOfSpecifiedUser(questionEntity, pageable);
        PageImpl<QuestionDTO> expectedQuestionsPage = getExpectedQuestionPage(questionEntity, pageable);
        //When
        Page<QuestionDTO> foundQuestions = questionsService.findAllByUserEmailIs("testEmail", pageable);
        //Then
        assertThat(foundQuestions).isEqualTo(expectedQuestionsPage);
    }

    private void mockingFindAllQuestionsOfSpecifiedUser(QuestionEntity questionEntity, PageRequest pageable) {
        List<QuestionEntity> questionEntityList = List.of(questionEntity);
        PageImpl<QuestionEntity> questionEntitiesPage = new PageImpl<>(questionEntityList, pageable, questionEntityList.size());
        when(questionsRepository.findAllByUserEntityEmailIs("testEmail", pageable)).thenReturn(questionEntitiesPage);
    }

    @Test
    @DisplayName("Find 5 newest questions")
    public void testFindFiveNewestQuestions() {
        //Given
        CategoryEntity categoryEntity = getCategoryEntity();
        UserEntity userEntity = getUserEntity();
        QuestionEntity questionEntity = getQuestionEntity(categoryEntity, userEntity);
        List<QuestionEntity> questionEntityList = List.of(questionEntity);
        when(questionsRepository.findTop5ByOrderByIdDesc()).thenReturn(questionEntityList);
        QuestionDTO expectedQuestionDTO = QuestionDTO.apply(questionEntity);
        List<QuestionDTO> expectedQuestionsList = List.of(expectedQuestionDTO);
        //When
        List<QuestionDTO> foundQuestions = questionsService.findTop5ByOrderByIdDesc();
        //Then
        assertThat(foundQuestions).isEqualTo(expectedQuestionsList);
    }
}
