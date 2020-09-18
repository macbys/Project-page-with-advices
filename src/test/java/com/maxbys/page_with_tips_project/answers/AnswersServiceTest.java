package com.maxbys.page_with_tips_project.answers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxbys.page_with_tips_project.questions.QuestionEntity;
import com.maxbys.page_with_tips_project.questions.QuestionsRepository;
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
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
class AnswersServiceTest {

    private AnswersService answersService;
    @Mock
    private AnswersRepository answersRepository;
    @Mock
    private QuestionsRepository questionsRepository;
    @Mock
    private UsersRepository usersRepository;

    @BeforeEach
    public void setUp() {
        answersService = new AnswersService(answersRepository, usersRepository, questionsRepository);
    }

    @Test
    @DisplayName("Find answer with existing id")
    void testFindAnswerWithExistingId() throws IOException {
        //Given
        AnswerEntity expectedAnswer = getAnswerEntity();
        when(answersRepository.findById(1L)).thenReturn(Optional.of(expectedAnswer));
        //When
        AnswerDTO foundAnswer = answersService.findById(1L);
        //Then
        assertThat(foundAnswer.getId()).isEqualTo(expectedAnswer.getId());
        assertThat(foundAnswer.getValue()).isEqualTo(expectedAnswer.getValue());
    }

    private AnswerEntity getAnswerEntity() throws IOException {
        File dataJsonAnswers = Paths.get("src", "test", "resources", "answers.json").toFile();
        AnswerEntity[] answers = new ObjectMapper().readValue(dataJsonAnswers, AnswerEntity[].class);
        return answers[0];
    }

    @Test
    @DisplayName("Throw error when looking for answer with non-existing id")
    void testThrowErrorWhenLookingForAnswerWithNonExistingId() {
        //Given
        long answerId = 1000L;
        when(answersRepository.findById(answerId)).thenReturn(Optional.empty());
        //When
        //Then
        assertThatThrownBy(() -> answersService.findById(answerId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Answer with id 1000 doesn't exist");
    }

    @Test
    @DisplayName("Enter answer repository save method when saving answer")
    public void testSavingAnswer() throws IOException{
        //Given
        UserEntity user = getUserEntity();
        when(usersRepository.findByEmail("testEmail1")).thenReturn(Optional.of(user));
        QuestionEntity question = getQuestionEntity();
        when(questionsRepository.findById(1L)).thenReturn(Optional.of(question));
        AnswerEntity expectedAnswer = getAnswerEntity();
        AnswerDTO answerDTO = AnswerDTO.apply(expectedAnswer);
        //When
        answersService.save(answerDTO);
        //Then
        verify(answersRepository, times(1)).save(expectedAnswer);
    }

    private UserEntity getUserEntity() throws IOException {
        File dataJsonUsers = Paths.get("src", "test", "resources", "users.json").toFile();
        UserEntity[] users = new ObjectMapper().readValue(dataJsonUsers, UserEntity[].class);
        return users[0];
    }

    private QuestionEntity getQuestionEntity() throws IOException {
        File dataJsonQuestions = Paths.get("src", "test", "resources", "questions.json").toFile();
        QuestionEntity[] questions = new ObjectMapper().readValue(dataJsonQuestions, QuestionEntity[].class);
        return questions[0];
    }

    @Test
    @DisplayName("Enter answers repository delete method")
    public void testDeleteAnswer() {
        //Given
        //When
        long answerId = 1L;
        answersService.deleteById(answerId);
        //Then
        verify(answersRepository, times(1)).deleteById(answerId);
    }

    @Test
    @DisplayName("Find all answers of question with specified id")
    public void testFindAllAnswersOfQuestionWithSpecifiedId() throws IOException{
        //Given
        PageRequest pageable = PageRequest.of(0, 5);
        AnswerEntity answerEntity = getAnswerEntity();
        Page<AnswerEntity> answersPage = getAnswerPage(pageable, answerEntity);
        long questionId = 1L;
        when(answersRepository.findAllByQuestionEntityId(questionId, pageable)).thenReturn(answersPage);
        Page<AnswerDTO> expectedAnswersPage = getExpectedAnswers(pageable, answerEntity);
        //When
        Page<AnswerDTO> foundAnswers = answersService.findAllByQuestionId(questionId, pageable);
        //Then
        assertThat(foundAnswers).isEqualTo(expectedAnswersPage);
    }

    private Page<AnswerEntity> getAnswerPage(PageRequest pageable, AnswerEntity answerEntity) {
        List<AnswerEntity> answersEntityList = List.of(answerEntity);
        return new PageImpl<>(answersEntityList, pageable, answersEntityList.size());
    }

    private Page<AnswerDTO> getExpectedAnswers(PageRequest pageable, AnswerEntity answerEntity) {
        AnswerDTO answerDTO = AnswerDTO.apply(answerEntity);
        List<AnswerDTO> answersList = List.of(answerDTO);
        return new PageImpl<>(answersList, pageable, answersList.size());
    }

    @Test
    @DisplayName("Find all answers of user with specified email")
    public void testFindAllAnswersOfUserWithSpecifiedEmail() throws IOException{
        //Given
        PageRequest pageable = PageRequest.of(0, 5);
        AnswerEntity answerEntity = getAnswerEntity();
        Page<AnswerEntity> answersPage = getAnswerPage(pageable, answerEntity);
        String email = "testEmail1";
        when(answersRepository.findAllByUserEntityEmail(email, pageable)).thenReturn(answersPage);
        Page<AnswerDTO> expectedAnswersPage = getExpectedAnswers(pageable, answerEntity);
        //When
        Page<AnswerDTO> foundAnswers = answersService.findAllByUserEmail(email, pageable);
        //Then
        assertThat(foundAnswers).isEqualTo(expectedAnswersPage);
    }
}
