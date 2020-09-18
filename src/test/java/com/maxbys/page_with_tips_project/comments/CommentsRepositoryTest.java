package com.maxbys.page_with_tips_project.comments;

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
class CommentsRepositoryTest {

    @Autowired
    private CommentsRepository commentsRepository;
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
    public void setUp() throws IOException, SQLException {
        DbTestUtil.resetAutoIncrementColumns(applicationContext, "comment_entity", "answer_entity", "question_entity");
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
        File dataJsonComments = Paths.get("src", "test", "resources", "comments.json").toFile();
        CommentEntity[] comments = new ObjectMapper().readValue(dataJsonComments, CommentEntity[].class);
        Arrays.stream(comments).forEach(commentsRepository::save);
    }

    @Test
    @DisplayName("Find comment by comment id")
    public void testFindCommentByCommentId() {
        //Given
        //When
        Optional<CommentEntity> commentEntityOptional = commentsRepository.findById(1L);
        //Then
        assertThat(commentEntityOptional.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Don't find comment with non-existing id")
    public void testDontFindCommentWithNonExistingId() {
        //Given
        //When
        Optional<CommentEntity> commentEntityOptional = commentsRepository.findById(1000L);
        //Then
        assertThat(commentEntityOptional.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Adding new comment")
    public void testAddingNewAnswer() {
        //Given
        CommentEntity expectedComment = getNewCommentEntity();
        //When
        CommentEntity savedComment = commentsRepository.save(expectedComment);
        //Then
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getValue()).isEqualTo(expectedComment.getValue());
        assertThat(commentsRepository.findAll().size()).isEqualTo(3);
    }

    private CommentEntity getNewCommentEntity() {
        CommentDTO commentDTO = CommentDTO.builder()
                .value("newValue")
                .build();
        Optional<UserEntity> userEntityOptional = usersRepository.findByEmail("testEmail1");
        UserEntity userEntity = userEntityOptional.get();
        Optional<AnswerEntity> answerEntityOptional = answersRepository.findById(1L);
        AnswerEntity answerEntity = answerEntityOptional.get();
        return CommentEntity.apply(commentDTO, answerEntity, userEntity);
    }

    @Test
    @DisplayName("Updating existing comment")
    public void testUpdatingExistingComment() {
        //Given
        CommentEntity expectedComment = getEditedCommentEntity();
        //When
        CommentEntity savedComment = commentsRepository.save(expectedComment);
        //Then
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getValue()).isEqualTo(expectedComment.getValue());
        assertThat(commentsRepository.findAll().size()).isEqualTo(2);
    }

    private CommentEntity getEditedCommentEntity() {
        CommentDTO commentDTO = CommentDTO.builder()
                .value("editedComment")
                .id(1L)
                .build();
        Optional<UserEntity> userEntityOptional = usersRepository.findByEmail("testEmail1");
        UserEntity userEntity = userEntityOptional.get();
        Optional<AnswerEntity> answerEntityOptional = answersRepository.findById(1L);
        AnswerEntity answerEntity = answerEntityOptional.get();
        return CommentEntity.apply(commentDTO, answerEntity, userEntity);
    }

    @Test
    @DisplayName("Deleting existing question")
    public void testDeletingExistingQuestion() {
        //Given
        long commentId = 1L;
        //When
        commentsRepository.deleteById(commentId);
        //Then
        assertThat(commentsRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Find all comments to answer with specified id")
    public void testFindAllCommentsToAnswerWithSpecifiedId() {
        //Given
        long answerId = 1;
        //When
        Page<CommentEntity> foundComments = commentsRepository.findAllByAnswerEntityId(answerId, PageRequest.of(0, 5));
        //Then
        assertThat(foundComments.getTotalElements()).isEqualTo(1);
        List<CommentEntity> foundCommentsList = foundComments.getContent();
        CommentEntity foundComment = foundCommentsList.get(0);
        AnswerEntity foundCommentAnswer = foundComment.getAnswerEntity();
        Long foundCommentAnswerId = foundCommentAnswer.getId();
        assertThat(foundCommentAnswerId).isEqualTo(answerId);
    }

    @Test
    @DisplayName("Find no comments to answer with non-existing id")
    public void testFindNoCommentsToAnswerWithNonExistingId() {
        //Given
        long answerId = 1000;
        //When
        Page<CommentEntity> foundComments = commentsRepository.findAllByAnswerEntityId(answerId, PageRequest.of(0, 5));
        //Then
        assertThat(foundComments.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("Find all comments of user with specified email")
    public void testFindAllCommentsOfUserWithSpecifiedEmail() {
        //Given
        String email = "testEmail1";
        //When
        Page<CommentEntity> foundComments = commentsRepository.findAllByUserEntity_Email(email, PageRequest.of(0, 5));
        //Then
        assertThat(foundComments.getTotalElements()).isEqualTo(1);
        List<CommentEntity> foundCommentsList = foundComments.getContent();
        CommentEntity foundComment = foundCommentsList.get(0);
        UserEntity foundCommentUser = foundComment.getUserEntity();
        String foundCommentUserEmail = foundCommentUser.getEmail();
        assertThat(foundCommentUserEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("Find no comments for user with non-existing email")
    public void testFindNoCommentsForUserWithNonExistingEmail() {
        //Given
        String email = "non-existing";
        //When
        Page<CommentEntity> foundComments = commentsRepository.findAllByUserEntity_Email(email, PageRequest.of(0, 5));
        //Then
        assertThat(foundComments.getTotalElements()).isEqualTo(0);
    }
}
