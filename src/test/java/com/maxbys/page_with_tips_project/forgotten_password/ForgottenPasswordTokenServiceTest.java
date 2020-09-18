package com.maxbys.page_with_tips_project.forgotten_password;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxbys.page_with_tips_project.mail_service.MailService;
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
import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
class ForgottenPasswordTokenServiceTest {

    private ForgottenPasswordTokenService forgottenPasswordTokenService;
    @Mock
    private ForgottenPasswordTokenRepository forgottenPasswordTokenRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private MailService mailService;
    @Mock
    private RandomStringGenerator randomStringGenerator;
    @Mock
    private TenMinutesExpiryDateGenerator tenMinutesExpiryDateGenerator;

    @BeforeEach
    public void setUp() {
        forgottenPasswordTokenService = new ForgottenPasswordTokenService(forgottenPasswordTokenRepository, usersRepository
                , mailService, randomStringGenerator, tenMinutesExpiryDateGenerator);
    }

    @Test
    @DisplayName("Return false while creating new token without mail service exception")
    public void testSaveNewToken() throws IOException{
        //Given
        String email = "testEmail1";
        UserEntity userEntity = getUserEntity();
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        String randomString = "fdsadfsdfsdgxv";
        when(randomStringGenerator.generateRandomUuid()).thenReturn(randomString);
        LocalDateTime localDateTime = LocalDateTime.now();
        when(tenMinutesExpiryDateGenerator.createTenMinutesExpiryDate()).thenReturn(localDateTime);
        ForgottenPasswordToken expectedToken = getForgottenPasswordToken(userEntity, randomString, localDateTime);
        //When
        boolean result = forgottenPasswordTokenService.sendResetLink(email);
        //Then
        assertThat(result).isFalse();
        verify(forgottenPasswordTokenRepository, times(1)).save(expectedToken);
    }

    private UserEntity getUserEntity() throws IOException {
        File dataJsonUsers = Paths.get("src", "test", "resources", "users.json").toFile();
        UserEntity[] users = new ObjectMapper().readValue(dataJsonUsers, UserEntity[].class);
        return users[0];
    }

    private ForgottenPasswordToken getForgottenPasswordToken(UserEntity userEntity, String randomString, LocalDateTime localDateTime) {
        return ForgottenPasswordToken.builder()
                .randomPathEnding(randomString)
                .userEntityId(new UserEntityEmbedded(userEntity))
                .expiryDate(localDateTime)
                .build();
    }

    @Test
    @DisplayName("Return true while creating new token with throwing mail service exception")
    public void testFailingToSendMessage() throws IOException, MessagingException {
        //Given
        String email = "testEmail1";
        UserEntity userEntity = getUserEntity();
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        String randomString = "fdsadfsdfsdgxv";
        when(randomStringGenerator.generateRandomUuid()).thenReturn(randomString);
        LocalDateTime localDateTime = LocalDateTime.now();
        when(tenMinutesExpiryDateGenerator.createTenMinutesExpiryDate()).thenReturn(localDateTime);
        String link = "http://localhost:8080/change-password/" + randomString + "?email=" + email;
        doThrow(new MessagingException()).when(mailService).sendMail(email, "Forgotten password", "<p>To change your password go to link below:</p><br>" +
                "<a href='" + link + "'>" + link + "</a>", true);
        //When
        boolean result = forgottenPasswordTokenService.sendResetLink(email);
        //Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Return nothing if token is valid")
    public void testReturnNothingIfTokenIsValid() throws IOException{
        //Given
        String email = "testEmail1";
        UserEntity userEntity = getUserEntity();
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        String randomString = "fdsadfsdfsdgxv";
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(10);
        ForgottenPasswordToken forgottenPasswordToken = getForgottenPasswordToken(userEntity, randomString, localDateTime);
        when(forgottenPasswordTokenRepository.findById(new UserEntityEmbedded(userEntity))).thenReturn(Optional.of(forgottenPasswordToken));
        //When
        String result = forgottenPasswordTokenService.checkTokenValidity(email, randomString);
        //Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Return error message for expired token")
    public void testResturnErrorMessageForExpiredToken() throws IOException{
        //Given
        String email = "testEmail1";
        UserEntity userEntity = getUserEntity();
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        String randomString = "fdsadfsdfsdgxv";
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(10);
        ForgottenPasswordToken forgottenPasswordToken = getForgottenPasswordToken(userEntity, randomString, localDateTime);
        when(forgottenPasswordTokenRepository.findById(new UserEntityEmbedded(userEntity))).thenReturn(Optional.of(forgottenPasswordToken));
        //When
        String result = forgottenPasswordTokenService.checkTokenValidity(email, randomString);
        //Then
        assertThat(result).isEqualTo("This link is expired");
    }

    @Test
    @DisplayName("Return error message for non-existing token")
    public void testReturnErrorMessageForNonExistingToken() throws IOException{
        //Given
        String email = "testEmail1";
        UserEntity userEntity = getUserEntity();
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        String randomString = "fdsadfsdfsdgxv";
        when(forgottenPasswordTokenRepository.findById(new UserEntityEmbedded(userEntity))).thenReturn(Optional.empty());
        //When
        String result = forgottenPasswordTokenService.checkTokenValidity(email, randomString);
        //Then
        assertThat(result).isEqualTo("There is no password change link for this email");
    }

    @Test
    @DisplayName("Return error message for wrong link")
    public void testReturnErrorMessageForWrongLink() throws IOException{
        //Given
        String email = "testEmail1";
        UserEntity userEntity = getUserEntity();
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        String randomString = "fdsadfsdfsdgxv";
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(10);
        ForgottenPasswordToken forgottenPasswordToken = getForgottenPasswordToken(userEntity, randomString, localDateTime);
        when(forgottenPasswordTokenRepository.findById(new UserEntityEmbedded(userEntity))).thenReturn(Optional.of(forgottenPasswordToken));
        String invalidRandomString = "invalid";
        //When
        String result = forgottenPasswordTokenService.checkTokenValidity(email, invalidRandomString);
        //Then
        assertThat(result).isEqualTo("Invalid link");
    }
}
