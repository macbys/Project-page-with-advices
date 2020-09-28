package com.maxbys.page_with_tips_project.ratings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxbys.page_with_tips_project.DbTestUtil;
import com.maxbys.page_with_tips_project.answers.AnswerEntity;
import com.maxbys.page_with_tips_project.answers.AnswersRepository;
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
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
class RatingsRepositoryTest {

    @Autowired
    private RatingsRepository ratingsRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private CategoriesRepository categoriesRepository;
    @Autowired
    private QuestionsRepository questionsRepository;
    @Autowired
    private AnswersRepository answersRepository;
    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    public void fillUpRatingsDataBase() throws IOException, SQLException {
        DbTestUtil.resetAutoIncrementColumns(applicationContext, "question_entity", "answer_entity", "user_entity");
        File dataJsonUsers = Paths.get("src", "test", "resources", "users.json").toFile();
        UserEntity[] users = new ObjectMapper().readValue(dataJsonUsers, UserEntity[].class);
        Arrays.stream(users).forEach(usersRepository::save);
        File dataJsonCategories = Paths.get("src", "test", "resources", "categories.json").toFile();
        CategoryEntity[] categories = new ObjectMapper().readValue(dataJsonCategories, CategoryEntity[].class);
        Arrays.stream(categories).forEach(categoriesRepository::save);
        File dataJsonQuestions = Paths.get("src", "test", "resources", "questions.json").toFile();
        QuestionEntity[] questions = new ObjectMapper().readValue(dataJsonQuestions, QuestionEntity[].class);
        Arrays.stream(questions).forEach(questionsRepository::save);
        File dataJsonAnswers = Paths.get("src", "test", "resources", "answers.json").toFile();
        AnswerEntity[] answers = new ObjectMapper().readValue(dataJsonAnswers, AnswerEntity[].class);
        Arrays.stream(answers).forEach(answersRepository::save);
        File dataJsonRatings = Paths.get("src", "test", "resources", "ratings.json").toFile();
        RatingEntity[] ratings = new ObjectMapper().readValue(dataJsonRatings, RatingEntity[].class);
        Arrays.stream(ratings).forEach(ratingsRepository::save);
    }

    @Test
    @DisplayName("Find rating by user email and answer id")
    public void testFindRatingByUserEmailAndAnswerId() {
        //Given
        UserEntity userEntity = usersRepository.findByEmail("testEmail1").get();
        AnswerEntity answerEntity = answersRepository.findById(1L).get();
        RatingId ratingId = new RatingId(userEntity, answerEntity);
        //When
        Optional<RatingEntity> ratingEntityOptional = ratingsRepository.findById(ratingId);
        //Then
        assertThat(ratingEntityOptional.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Don't find non-existing rating")
    public void testDontFindNonExistingRating() {
        //Given
        UserEntity userEntity = usersRepository.findByEmail("testEmail1").get();
        AnswerEntity answerEntity = answersRepository.findById(2L).get();
        RatingId ratingId = new RatingId(userEntity, answerEntity);
        //When
        Optional<RatingEntity> ratingEntityOptional = ratingsRepository.findById(ratingId);
        //Then
        assertThat(ratingEntityOptional.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Save new rating")
    public void testSaveNewRating() {
        //Given
        byte ratingValue = 1;
        UserEntity userEntity = usersRepository.findByEmail("testEmail1").get();
        AnswerEntity answerEntity = answersRepository.findById(2L).get();
        RatingEntity expectedRatingEntity = RatingEntity.apply(ratingValue, userEntity, answerEntity);
        //When
        RatingEntity savedRatingEntity = ratingsRepository.save(expectedRatingEntity);
        //Then
        assertThat(ratingsRepository.findAll().size()).isEqualTo(3);
        assertThat(savedRatingEntity).isEqualTo(expectedRatingEntity);
    }

    @Test
    @DisplayName("Update existing rating")
    public void testUpdateExistingRating() {
        //Given
        byte ratingValue = -1;
        UserEntity userEntity = usersRepository.findByEmail("testEmail1").get();
        AnswerEntity answerEntity = answersRepository.findById(1L).get();
        RatingEntity expectedRatingEntity = RatingEntity.apply(ratingValue, userEntity, answerEntity);
        //When
        RatingEntity savedRatingEntity = ratingsRepository.save(expectedRatingEntity);
        //Then
        assertThat(ratingsRepository.findAll().size()).isEqualTo(2);
        assertThat(savedRatingEntity).isEqualTo(expectedRatingEntity);
    }

    @Test
    @DisplayName("Delete existing rating")
    public void testDeleteExistingRating(){
        //Given
        UserEntity userEntity = usersRepository.findByEmail("testEmail1").get();
        AnswerEntity answerEntity = answersRepository.findById(1L).get();
        RatingId ratingId = new RatingId(userEntity, answerEntity);
        //When
        ratingsRepository.deleteById(ratingId);
        //Then
        assertThat(ratingsRepository.findAll().size()).isEqualTo(1);
    }
}
