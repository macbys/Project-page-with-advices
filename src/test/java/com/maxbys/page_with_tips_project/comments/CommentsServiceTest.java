package com.maxbys.page_with_tips_project.comments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxbys.page_with_tips_project.answers.AnswerEntity;
import com.maxbys.page_with_tips_project.answers.AnswersRepository;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
class CommentsServiceTest {

    private CommentsService commentsService;
    @Mock
    private CommentsRepository commentsRepository;
    @Mock
    private AnswersRepository answersRepository;
    @Mock
    private UsersRepository usersRepository;

    @BeforeEach
    public void setUp() {
        commentsService = new CommentsService(commentsRepository, usersRepository, answersRepository);
    }

    @Test
    @DisplayName("Find comment with existing id")
    void testFindCommentWithExistingId() throws IOException {
        //Given
        CommentEntity expectedComment = getCommentEntity();
        long commentId = 1L;
        when(commentsRepository.findById(commentId)).thenReturn(Optional.of(expectedComment));
        //When
        CommentDTO foundComment = commentsService.findById(commentId);
        //Then
        assertThat(foundComment.getId()).isEqualTo(expectedComment.getId());
        assertThat(foundComment.getValue()).isEqualTo(expectedComment.getValue());
    }

    private CommentEntity getCommentEntity() throws IOException {
        File dataJsonComments = Paths.get("src", "test", "resources", "comments.json").toFile();
        CommentEntity[] comments = new ObjectMapper().readValue(dataJsonComments, CommentEntity[].class);
        return comments[0];
    }

    @Test
    @DisplayName("Throw error when looking for comment with non-existing id")
    void testThrowErrorWhenLookingForCommentWithNonExistingId() {
        //Given
        long commentId = 1000L;
        when(commentsRepository.findById(commentId)).thenReturn(Optional.empty());
        //When
        //Then
        assertThatThrownBy(() -> commentsService.findById(commentId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Comment with id 1000 doesn't exist");
    }

    @Test
    @DisplayName("Enter comment repository save method when saving comment")
    public void testSavingComment() throws IOException{
        //Given
        UserEntity user = getUserEntity();
        String email = "testEmail1";
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(user));
        AnswerEntity answer = getAnswerEntity();
        long answerId = 1L;
        when(answersRepository.findById(answerId)).thenReturn(Optional.of(answer));
        CommentEntity expectedComment = getCommentEntity();
        CommentDTO commentDTO = CommentDTO.apply(expectedComment);
        //When
        commentsService.save(commentDTO, answerId, email);
        //Then
        verify(commentsRepository, times(1)).save(expectedComment);
    }

    private UserEntity getUserEntity() throws IOException {
        File dataJsonUsers = Paths.get("src", "test", "resources", "users.json").toFile();
        UserEntity[] users = new ObjectMapper().readValue(dataJsonUsers, UserEntity[].class);
        return users[0];
    }

    private AnswerEntity getAnswerEntity() throws IOException {
        File dataJsonAnswers = Paths.get("src", "test", "resources", "answers.json").toFile();
        AnswerEntity[] answers = new ObjectMapper().readValue(dataJsonAnswers, AnswerEntity[].class);
        return answers[0];
    }

    @Test
    @DisplayName("Enter comments repository delete method")
    public void testDeleteComment() {
        //Given
        //When
        long commentId = 1L;
        commentsService.deleteById(commentId);
        //Then
        verify(commentsRepository, times(1)).deleteById(commentId);
    }

    @Test
    @DisplayName("Find all comments of answer with specified id")
    public void testFindAllCommentsOfAnswerWithSpecifiedId() throws IOException{
        //Given
        PageRequest pageable = PageRequest.of(0, 5);
        CommentEntity commentEntity = getCommentEntity();
        Page<CommentEntity> commentEntityPage = getCommentEntities(pageable, commentEntity);
        long answerId = 1L;
        when(commentsRepository.findAllByAnswerEntityId(answerId, pageable)).thenReturn(commentEntityPage);
        Page<CommentDTO> expectedCommentPage = getCommentDTOS(pageable, commentEntity);
        //When
        Page<CommentDTO> foundComments = commentsService.findAllByAnswerId(answerId, pageable);
        //Then
        assertThat(foundComments).isEqualTo(expectedCommentPage);
    }

    private Page<CommentEntity> getCommentEntities(PageRequest pageable, CommentEntity commentEntity) {
        List<CommentEntity> commentEntityList = List.of(commentEntity);
        return new PageImpl<>(commentEntityList, pageable, commentEntityList.size());
    }

    private Page<CommentDTO> getCommentDTOS(PageRequest pageable, CommentEntity commentEntity) {
        CommentDTO commentDTO = CommentDTO.apply(commentEntity);
        List<CommentDTO> commentsList = List.of(commentDTO);
        return new PageImpl<>(commentsList, pageable, commentsList.size());
    }

    @Test
    @DisplayName("Find all comments of user with specified email")
    public void testFindAllCommentsOfUserWithSpecifiedEmail() throws IOException{
        //Given
        PageRequest pageable = PageRequest.of(0, 5);
        CommentEntity commentEntity = getCommentEntity();
        Page<CommentEntity> commentEntityPage = getCommentEntities(pageable, commentEntity);
        String email = "testEmail1";
        when(commentsRepository.findAllByUserEntity_Email(email, pageable)).thenReturn(commentEntityPage);
        Page<CommentDTO> expectedCommentPage = getCommentDTOS(pageable, commentEntity);
        //When
        Page<CommentDTO> foundComments = commentsService.findAllByUser_Email(email, pageable);
        //Then
        assertThat(foundComments).isEqualTo(expectedCommentPage);
    }
}
