package com.maxbys.page_with_tips_project.answers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxbys.page_with_tips_project.DbTestUtil;
import com.maxbys.page_with_tips_project.categories.CategoriesRepository;
import com.maxbys.page_with_tips_project.categories.CategoryEntity;
import com.maxbys.page_with_tips_project.questions.QuestionEntity;
import com.maxbys.page_with_tips_project.questions.QuestionsRepository;
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
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
class AnswersRepositoryTest {

    @Autowired
    private AnswersRepository answersRepository;
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
        DbTestUtil.resetAutoIncrementColumns(applicationContext, "answer_entity", "question_entity", "user_entity");
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
    }

    @Test
    @DisplayName("Find answer by answer id")
    public void testFindAnswerByAnswerId() {
        //Given
        //When
        Optional<AnswerEntity> answerEntityOptional = answersRepository.findById(1L);
        //Then
        assertThat(answerEntityOptional.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Don't find answer with non-existing id")
    public void testDontFindAnswerWithNonExistingId() {
        //Given
        //When
        Optional<AnswerEntity> answerEntityOptional = answersRepository.findById(1000L);
        //Then
        assertThat(answerEntityOptional.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Adding new answer")
    public void testAddingNewAnswer() {
        //Given
        AnswerDTO answerDTO = AnswerDTO.builder()
                .value("newQuestion")
                .build();
        Optional<UserEntity> userEntityOptional = usersRepository.findByEmail("testEmail2");
        UserEntity userEntity = userEntityOptional.get();
        Optional<QuestionEntity> questionEntityOptional = questionsRepository.findById(1L);
        QuestionEntity questionEntity = questionEntityOptional.get();
        AnswerEntity expectedAnswer = AnswerEntity.apply(answerDTO, userEntity, questionEntity);
        //When
        AnswerEntity savedAnswer = answersRepository.save(expectedAnswer);
        //Then
        assertThat(savedAnswer.getId()).isNotNull();
        assertThat(savedAnswer.getValue()).isEqualTo(expectedAnswer.getValue());
        assertThat(answersRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Updating existing question")
    public void testUpdatingExistingQuestion() {
        //Given
        AnswerDTO answerDTO = AnswerDTO.builder()
                .value("editedAnswer")
                .id(1L)
                .build();
        Optional<UserEntity> userEntityOptional = usersRepository.findByEmail("testEmail1");
        UserEntity userEntity = userEntityOptional.get();
        Optional<QuestionEntity> questionEntityOptional = questionsRepository.findById(1L);
        QuestionEntity questionEntity = questionEntityOptional.get();
        AnswerEntity expectedAnswer = AnswerEntity.apply(answerDTO, userEntity, questionEntity);
        //When
        AnswerEntity savedAnswer = answersRepository.save(expectedAnswer);
        //Then
        assertThat(savedAnswer.getId()).isNotNull();
        assertThat(savedAnswer.getValue()).isEqualTo(expectedAnswer.getValue());
        assertThat(answersRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Deleting existing question")
    public void testDeletingExistingQuestion() {
        //Given
        long answerId = 1L;
        //When
        answersRepository.deleteById(answerId);
        //Then
        assertThat(answersRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Find all answers to question with specified id")
    public void testFindAllAnswersToQuestionWithSpecifiedId() {
        //Given
        long questionId = 1;
        //When
        Page<AnswerEntity> foundAnswers = answersRepository.findAllByQuestionEntityId(questionId, PageRequest.of(0, 5));
        //Then
        assertThat(foundAnswers.getTotalElements()).isEqualTo(1);
        List<AnswerEntity> foundAnswersList = foundAnswers.getContent();
        AnswerEntity foundAnswer = foundAnswersList.get(0);
        QuestionEntity foundAnswerQuestion = foundAnswer.getQuestionEntity();
        Long foundAnswerQuestionId = foundAnswerQuestion.getId();
        assertThat(foundAnswerQuestionId).isEqualTo(questionId);
    }

    @Test
    @DisplayName("Find no answers to question with non-existing id")
    public void testFindNoAnswersToQuestionWithNonExistingId() {
        //Given
        long questionId = 1000;
        //When
        Page<AnswerEntity> foundAnswers = answersRepository.findAllByQuestionEntityId(questionId, PageRequest.of(0, 5));
        //Then
        assertThat(foundAnswers.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("Find all answers of user with specified email")
    public void testFindAllAnswersOfUserWithSpecifiedEmail() {
        //Given
        String email = "testEmail1";
        //When
        Page<AnswerEntity> foundAnswers = answersRepository.findAllByUserEntityEmail(email, PageRequest.of(0, 5));
        //Then
        assertThat(foundAnswers.getTotalElements()).isEqualTo(1);
        List<AnswerEntity> foundAnswersList = foundAnswers.getContent();
        AnswerEntity foundAnswer = foundAnswersList.get(0);
        UserEntity foundAnswerUser = foundAnswer.getUserEntity();
        String foundAnswerUserEmail = foundAnswerUser.getEmail();
        assertThat(foundAnswerUserEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("Find no answers for user with non-existing email")
    public void testFindNoAnswersForUserWithNonExistingEmail() {
        //Given
        String email = "non-existing";
        //When
        Page<AnswerEntity> foundAnswers = answersRepository.findAllByUserEntityEmail(email, PageRequest.of(0, 5));
        //Then
        assertThat(foundAnswers.getTotalElements()).isEqualTo(0);
    }
}
