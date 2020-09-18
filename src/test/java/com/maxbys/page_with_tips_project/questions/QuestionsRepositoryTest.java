package com.maxbys.page_with_tips_project.questions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxbys.page_with_tips_project.DbTestUtil;
import com.maxbys.page_with_tips_project.categories.CategoriesRepository;
import com.maxbys.page_with_tips_project.categories.CategoryEntity;
import com.maxbys.page_with_tips_project.users.UserEntity;
import com.maxbys.page_with_tips_project.users.UsersRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
class QuestionsRepositoryTest {

    @Autowired
    private QuestionsRepository questionsRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private CategoriesRepository categoriesRepository;
    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    public void setUp() throws IOException, SQLException {
        DbTestUtil.resetAutoIncrementColumns(applicationContext, "question_entity");
        File dataJsonUsers = Paths.get("src", "test", "resources", "users.json").toFile();
        UserEntity[] users = new ObjectMapper().readValue(dataJsonUsers, UserEntity[].class);
        Arrays.stream(users).forEach(usersRepository::save);
        File dataJsonCategories = Paths.get("src", "test", "resources", "categories.json").toFile();
        CategoryEntity[] categories = new ObjectMapper().readValue(dataJsonCategories, CategoryEntity[].class);
        Arrays.stream(categories).forEach(categoriesRepository::save);
        File dataJsonQuestions = Paths.get("src", "test", "resources", "questions.json").toFile();
        QuestionEntity[] questions = new ObjectMapper().readValue(dataJsonQuestions, QuestionEntity[].class);
        Arrays.stream(questions).forEach(questionsRepository::save);
    }

    @Test
    @DisplayName("Find question by question id")
    public void testFindQuestionByQuestionId() {
        //Given
        //When
        Optional<QuestionEntity> questionEntityOptional = questionsRepository.findById(1L);
        //Then
        assertThat(questionEntityOptional.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Don't find question with non-existing id")
    public void testDontFindQuestionWithNonExistingId() {
        //Given
        //When
        Optional<QuestionEntity> questionEntityOptional = questionsRepository.findById(1000L);
        //Then
        assertThat(questionEntityOptional.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Adding new question")
    public void testAddingNewQuestion() {
        //Given
        FormQuestionTemplate newFormQuestionTemplate = FormQuestionTemplate.builder()
                .questionValue("newQuestionValue")
                .build();
        Optional<UserEntity> userEntityOptional = usersRepository.findByEmail("testEmail2");
        UserEntity userEntity = userEntityOptional.get();
        Optional<CategoryEntity> categoryEntityOptional = categoriesRepository.findById("testName2");
        CategoryEntity categoryEntity = categoryEntityOptional.get();
        QuestionEntity expectedQuestionEntity = QuestionEntity.apply(newFormQuestionTemplate, userEntity, categoryEntity);
        //When
        QuestionEntity savedQuestionEntity = questionsRepository.save(expectedQuestionEntity);
        //Then
        assertThat(savedQuestionEntity.getId()).isNotNull();
        assertThat(savedQuestionEntity.getValue()).isEqualTo(expectedQuestionEntity.getValue());
        assertThat(questionsRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Updating existing question")
    public void testUpdatingExistingQuestion() {
        //Given
        FormQuestionTemplate newFormQuestionTemplate = FormQuestionTemplate.builder()
                .id(1L)
                .questionValue("newQuestionValue")
                .build();
        Optional<UserEntity> userEntityOptional = usersRepository.findByEmail("testEmail2");
        UserEntity userEntity = userEntityOptional.get();
        Optional<CategoryEntity> categoryEntityOptional = categoriesRepository.findById("testName2");
        CategoryEntity categoryEntity = categoryEntityOptional.get();
        QuestionEntity expectedQuestionEntity = QuestionEntity.apply(newFormQuestionTemplate, userEntity, categoryEntity);
        //When
        QuestionEntity savedQuestionEntity = questionsRepository.save(expectedQuestionEntity);
        //Then
        assertThat(savedQuestionEntity.getId()).isNotNull();
        assertThat(savedQuestionEntity.getValue()).isEqualTo(expectedQuestionEntity.getValue());
        assertThat(questionsRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Find all questions with specified category")
    public void testFindAllQuestionsWithSpecifiedCategory() {
        //Given
        String categoryName = "testName2";
        //When
        Page<QuestionEntity> foundQuestionEntities = questionsRepository.findAllByCategoryEntityNameIs(categoryName, PageRequest.of(0, 5));
        //Then
        assertThat(foundQuestionEntities.getTotalElements()).isEqualTo(1);
        List<QuestionEntity> foundQuestionsList = foundQuestionEntities.getContent();
        QuestionEntity foundQuestion = foundQuestionsList.get(0);
        CategoryEntity foundQuestionCategory = foundQuestion.getCategoryEntity();
        String foundQuestionCategoryName = foundQuestionCategory.getName();
        assertThat(foundQuestionCategoryName).isEqualTo(categoryName);
    }

    @Test
    @DisplayName("Find no questions for non-existing category")
    public void testFindNoQuestionsForNonExistingCategory() {
        //Given
        String categoryName = "non-existing";
        //When
        Page<QuestionEntity> foundQuestionEntities = questionsRepository.findAllByCategoryEntityNameIs(categoryName, PageRequest.of(0, 5));
        //Then
        assertThat(foundQuestionEntities.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("Find all questions of specified user")
    public void testFindAllQuestionsOfSpecifiedUser() {
        //Given
        String email = "testEmail2";
        //When
        Page<QuestionEntity> foundQuestionEntities = questionsRepository.findAllByUserEntityEmailIs(email, PageRequest.of(0, 5));
        //Then
        assertThat(foundQuestionEntities.getTotalElements()).isEqualTo(1);
        List<QuestionEntity> foundQuestionsList = foundQuestionEntities.getContent();
        QuestionEntity foundQuestion = foundQuestionsList.get(0);
        UserEntity foundQuestionUser = foundQuestion.getUserEntity();
        String foundQuestionUserEmail = foundQuestionUser.getEmail();
        assertThat(foundQuestionUserEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("Find no questions for non-existing user")
    public void testFindNoQuestionsForNonExistingUser() {
        //Given
        String email = "non-existing";
        //When
        Page<QuestionEntity> foundQuestionEntities = questionsRepository.findAllByUserEntityEmailIs(email, PageRequest.of(0, 5));
        //Then
        assertThat(foundQuestionEntities.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("Get questions sorted by id descending")
    public void testGetQuestionsSortedByIdDescending() {
        //Given
        //When
        List<QuestionEntity> sortedQuestions = questionsRepository.findTop5ByOrderByIdDesc();
        //Then
        QuestionEntity firstQuestion = sortedQuestions.get(0);
        Long firstId = firstQuestion.getId();
        QuestionEntity secondQuestion = sortedQuestions.get(1);
        Long secondId = secondQuestion.getId();
        long secondIdIncreasedByOne = secondId + 1;
        assertThat(firstId == secondIdIncreasedByOne).isTrue();
    }
}
