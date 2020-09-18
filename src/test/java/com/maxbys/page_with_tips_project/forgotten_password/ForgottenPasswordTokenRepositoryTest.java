package com.maxbys.page_with_tips_project.forgotten_password;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxbys.page_with_tips_project.users.UserEntity;
import com.maxbys.page_with_tips_project.users.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@RunWith(SpringJUnit4ClassRunner.class)
class ForgottenPasswordTokenRepositoryTest {

    @Autowired
    private ForgottenPasswordTokenRepository forgottenPasswordTokenRepository;
    @Autowired
    private UsersRepository usersRepository;

    @BeforeEach
    public void setUp() throws IOException{
        File dataJsonUsers = Paths.get("src", "test", "resources", "users.json").toFile();
        UserEntity[] users = new ObjectMapper().readValue(dataJsonUsers, UserEntity[].class);
        Arrays.stream(users).forEach(usersRepository::save);
        File dataJsonForgottenPasswordTokens = Paths.get("src", "test", "resources", "forgottenPasswordTokens.json").toFile();
        ForgottenPasswordToken[] forgottenPasswordTokens = new ObjectMapper().readValue(dataJsonForgottenPasswordTokens, ForgottenPasswordToken[].class);
        Arrays.stream(forgottenPasswordTokens).forEach(forgottenPasswordTokenRepository::save);
    }

    @Test
    @DisplayName("Save new forgotten password token")
    public void testSaveNewForgottenPasswordToken() {
        //Given
        UserEntity userEntity = usersRepository.findByEmail("testEmail2").get();
        ForgottenPasswordToken expectedToken = ForgottenPasswordToken.builder()
                .userEntityId(new UserEntityEmbedded(userEntity))
                .randomPathEnding("fdsfdsa")
                .build();
        //When
        ForgottenPasswordToken savedToken = forgottenPasswordTokenRepository.save(expectedToken);
        //Then
        assertThat(savedToken).isEqualTo(expectedToken);
        assertThat(forgottenPasswordTokenRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Update existing forgotten password token")
    public void testUpdateExistingForgottenPasswordToken() {
        //Given
        UserEntity userEntity = usersRepository.findByEmail("testEmail1").get();
        ForgottenPasswordToken expectedToken = ForgottenPasswordToken.builder()
                .userEntityId(new UserEntityEmbedded(userEntity))
                .randomPathEnding("editedValue")
                .build();
        //When
        ForgottenPasswordToken savedToken = forgottenPasswordTokenRepository.save(expectedToken);
        //Then
        assertThat(savedToken).isEqualTo(expectedToken);
        assertThat(forgottenPasswordTokenRepository.findAll().size()).isEqualTo(1);
    }
}
