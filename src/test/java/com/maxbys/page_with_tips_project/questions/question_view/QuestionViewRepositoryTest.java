package com.maxbys.page_with_tips_project.questions.question_view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxbys.page_with_tips_project.DbTestUtil;
import com.maxbys.page_with_tips_project.categories.CategoriesRepository;
import com.maxbys.page_with_tips_project.categories.CategoryEntity;
import com.maxbys.page_with_tips_project.questions.QuestionEntity;
import com.maxbys.page_with_tips_project.questions.QuestionsRepository;
import com.maxbys.page_with_tips_project.users.UserEntity;
import com.maxbys.page_with_tips_project.users.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class QuestionViewRepositoryTest {

    @Autowired
    private QuestionViewRepository questionViewRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private CategoriesRepository categoriesRepository;
    @Autowired
    private QuestionsRepository questionsRepository;
    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    public void setUp() throws SQLException, IOException {
        DbTestUtil.resetAutoIncrementColumns(applicationContext, "question_entity", "user_entity");
        File dataJsonUsers = Paths.get("src", "test", "resources", "users.json").toFile();
        UserEntity[] users = new ObjectMapper().readValue(dataJsonUsers, UserEntity[].class);
        Arrays.stream(users).forEach(usersRepository::save);
        File dataJsonCategories = Paths.get("src", "test", "resources", "categories.json").toFile();
        CategoryEntity[] categories = new ObjectMapper().readValue(dataJsonCategories, CategoryEntity[].class);
        Arrays.stream(categories).forEach(categoriesRepository::save);
        File dataJsonQuestions = Paths.get("src", "test", "resources", "questions.json").toFile();
        QuestionEntity[] questions = new ObjectMapper().readValue(dataJsonQuestions, QuestionEntity[].class);
        Arrays.stream(questions).forEach(questionsRepository::save);
        File dataJsonQuestionViwes = Paths.get("src", "test", "resources", "questionViews.json").toFile();
        QuestionView[] questionViews = new ObjectMapper().readValue(dataJsonQuestionViwes, QuestionView[].class);
        Arrays.stream(questionViews).forEach(qv -> qv.setCreationTime(new Date()));
        Arrays.stream(questionViews).forEach(questionViewRepository::save);
    }

    @Test
    @DisplayName("Add new question view")
    public void testAddNewQuestionView() {
        //Given
        QuestionView expectedQuestionView = getQuestionView();
        //When
        QuestionView savedQuestionView = questionViewRepository.save(expectedQuestionView);
        //Then
        assertThat(savedQuestionView.getId()).isNotNull();
        List<QuestionView> addQuestionViews = questionViewRepository.findAll();
        int size = addQuestionViews.size();
        assertThat(size).isEqualTo(3);
    }

    private QuestionView getQuestionView() {
        UserEntity userEntity = usersRepository.findByEmail("testEmail1").get();
        QuestionEntity questionEntity = questionsRepository.findById(1L).get();
        return QuestionView.builder()
                .userEntity(userEntity)
                .question(questionEntity)
                .creationTime(new Date())
                .build();
    }

    @Test
    @DisplayName("Find questions sorted by views today")
    public void testFindQuestionsSortedByViewsToday() {
        //Given
        QuestionView questionView = getQuestionView();
        questionViewRepository.save(questionView);
        //When
        Page<QuestionEntity> mostPopularQuestionsToday = questionViewRepository.getMostPopularQuestionsToday(PageRequest.of(0, 5));
        //Then
        assertThat(mostPopularQuestionsToday.getTotalElements()).isEqualTo(2);
        QuestionEntity firstQuestion = mostPopularQuestionsToday.getContent().get(0);
        assertThat(firstQuestion.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Find questions sorted by views collected during 7 days")
    public void testFindQuestionsSortedByViewsCollectedDuringSevenDays() {
        //Given
        QuestionView questionView = getQuestionView();
        LocalDate today = LocalDate.now();
        java.sql.Date todayMinusTenDays = java.sql.Date.valueOf(today.minusDays(10));
        questionView.setCreationTime(todayMinusTenDays);
        questionViewRepository.save(questionView);
        java.sql.Date todayMinusSixDays = java.sql.Date.valueOf(today.minusDays(6));
        //When
        Page<QuestionEntity> mostPopularQuestionsByViewsCollectedDuringSevenDays = questionViewRepository.getMostPopularQuestionsFromTheDay(todayMinusSixDays, PageRequest.of(0, 5));
        //Then
        QuestionEntity firstQuestion = mostPopularQuestionsByViewsCollectedDuringSevenDays.getContent().get(0);
        assertThat(firstQuestion.getId()).isEqualTo(2L);
    }
}
