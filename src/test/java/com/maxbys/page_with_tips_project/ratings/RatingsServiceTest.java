package com.maxbys.page_with_tips_project.ratings;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
class RatingsServiceTest {

    private RatingsService ratingsService;
    @Mock
    private RatingsRepository ratingsRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private AnswersRepository answersRepository;

    @BeforeEach
    public void setUp() {
        ratingsService = new RatingsService(ratingsRepository, usersRepository, answersRepository);
    }

    @Test
    @DisplayName("Enter rating repository save method to save new rating up and increase answer rating by 1")
    public void testAddNewRatingUp() throws IOException {
        //Given
        String email = "testEmail1";
        UserEntity userEntity = getUserEntity();
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        Long answerId = 1L;
        AnswerEntity answerEntity = getAnswerEntity();
        when(answersRepository.findById(answerId)).thenReturn(Optional.of(answerEntity));
        RatingId ratingId = new RatingId(userEntity, answerEntity);
        when(ratingsRepository.findById(ratingId)).thenReturn(Optional.empty());
        byte ratingValue = 1;
        RatingEntity ratingEntity = RatingEntity.apply(ratingValue, userEntity, answerEntity);
        //When
        ratingsService.addRateUp(email, answerId);
        //Then
        verify(ratingsRepository, times(1)).save(ratingEntity);
        int answerRatingIncreasedByOne = answerEntity.getRating() + 1;
        answerEntity.setRating(answerRatingIncreasedByOne);
        verify(answersRepository, times(1)).save(answerEntity);
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
    @DisplayName("Delete rating up while trying to add the same rating and decrease answer rating by 1")
    public void testCancelRatingUp() throws IOException {
        //Given
        String email = "testEmail1";
        UserEntity userEntity = getUserEntity();
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        Long answerId = 1L;
        AnswerEntity answerEntity = getAnswerEntity();
        when(answersRepository.findById(answerId)).thenReturn(Optional.of(answerEntity));
        RatingId ratingId = new RatingId(userEntity, answerEntity);
        byte ratingValue = 1;
        RatingEntity ratingEntity = RatingEntity.apply(ratingValue, userEntity, answerEntity);
        when(ratingsRepository.findById(ratingId)).thenReturn(Optional.of(ratingEntity));
        //When
        ratingsService.addRateUp(email, answerId);
        //Then
        verify(ratingsRepository, times(1)).deleteById(ratingId);
        int answerRatingDecreasedByOne = answerEntity.getRating() - 1;
        answerEntity.setRating(answerRatingDecreasedByOne);
        verify(answersRepository, times(1)).save(answerEntity);
    }

    @Test
    @DisplayName("Enter rating repository save method to replace existing down rating with up one and increase answer rating by 2")
    public void testRateUpAnswerRatedDown() throws IOException{
        //Given
        String email = "testEmail1";
        UserEntity userEntity = getUserEntity();
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        Long answerId = 1L;
        AnswerEntity answerEntity = getAnswerEntity();
        when(answersRepository.findById(answerId)).thenReturn(Optional.of(answerEntity));
        RatingId ratingId = new RatingId(userEntity, answerEntity);
        byte negativeRatingValue = -1;
        RatingEntity ratingEntity = RatingEntity.apply(negativeRatingValue, userEntity, answerEntity);
        when(ratingsRepository.findById(ratingId)).thenReturn(Optional.of(ratingEntity));
        byte positiveRatingValue = 1;
        RatingEntity expectedRatingEntity = RatingEntity.apply(positiveRatingValue, userEntity, answerEntity);
        //When
        ratingsService.addRateUp(email, answerId);
        //Then
        verify(ratingsRepository, times(1)).save(expectedRatingEntity);
        int answerRatingIncreasedByTwo = answerEntity.getRating() + 2;
        answerEntity.setRating(answerRatingIncreasedByTwo);
        verify(answersRepository, times(1)).save(answerEntity);
    }

    @Test
    @DisplayName("Enter rating repository save method to save new rating down and decrease answer rating by 1")
    public void testAddNewRatingDown() throws IOException {
        //Given
        String email = "testEmail1";
        UserEntity userEntity = getUserEntity();
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        Long answerId = 1L;
        AnswerEntity answerEntity = getAnswerEntity();
        when(answersRepository.findById(answerId)).thenReturn(Optional.of(answerEntity));
        RatingId ratingId = new RatingId(userEntity, answerEntity);
        when(ratingsRepository.findById(ratingId)).thenReturn(Optional.empty());
        byte ratingValue = -1;
        RatingEntity ratingEntity = RatingEntity.apply(ratingValue, userEntity, answerEntity);
        //When
        ratingsService.addRateDown(email, answerId);
        //Then
        verify(ratingsRepository, times(1)).save(ratingEntity);
        int answerRatingDecreasedByOne = answerEntity.getRating() - 1;
        answerEntity.setRating(answerRatingDecreasedByOne);
        verify(answersRepository, times(1)).save(answerEntity);
    }

    @Test
    @DisplayName("Delete rating down while trying to add the same rating and increase answer rating by 1")
    public void testCancelRatingDown() throws IOException {
        //Given
        String email = "testEmail1";
        UserEntity userEntity = getUserEntity();
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        Long answerId = 1L;
        AnswerEntity answerEntity = getAnswerEntity();
        when(answersRepository.findById(answerId)).thenReturn(Optional.of(answerEntity));
        RatingId ratingId = new RatingId(userEntity, answerEntity);
        byte ratingValue = -1;
        RatingEntity ratingEntity = RatingEntity.apply(ratingValue, userEntity, answerEntity);
        when(ratingsRepository.findById(ratingId)).thenReturn(Optional.of(ratingEntity));
        //When
        ratingsService.addRateDown(email, answerId);
        //Then
        verify(ratingsRepository, times(1)).deleteById(ratingId);
        int answerRatingIncreasedByOne = answerEntity.getRating() + 1;
        answerEntity.setRating(answerRatingIncreasedByOne);
        verify(answersRepository, times(1)).save(answerEntity);
    }

    @Test
    @DisplayName("Enter rating repository save method to replace existing up rating with down one and decrease answer rating by 2")
    public void testRateDownAnswerRatedUp() throws IOException{
        //Given
        String email = "testEmail1";
        UserEntity userEntity = getUserEntity();
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        Long answerId = 1L;
        AnswerEntity answerEntity = getAnswerEntity();
        when(answersRepository.findById(answerId)).thenReturn(Optional.of(answerEntity));
        RatingId ratingId = new RatingId(userEntity, answerEntity);
        byte positiveRatingValue = 1;
        RatingEntity ratingEntity = RatingEntity.apply(positiveRatingValue, userEntity, answerEntity);
        when(ratingsRepository.findById(ratingId)).thenReturn(Optional.of(ratingEntity));
        byte negativeRatingValue = -1;
        RatingEntity expectedRatingEntity = RatingEntity.apply(negativeRatingValue, userEntity, answerEntity);
        //When
        ratingsService.addRateDown(email, answerId);
        //Then
        verify(ratingsRepository, times(1)).save(expectedRatingEntity);
        int answerRatingDecreasedByTwo = answerEntity.getRating() - 2;
        answerEntity.setRating(answerRatingDecreasedByTwo);
        verify(answersRepository, times(1)).save(answerEntity);
    }
}
