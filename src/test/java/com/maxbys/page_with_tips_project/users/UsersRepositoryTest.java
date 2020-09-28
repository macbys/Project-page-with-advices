package com.maxbys.page_with_tips_project.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxbys.page_with_tips_project.DbTestUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
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
class UsersRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    public void fillUpDatabase() throws IOException, SQLException {
        DbTestUtil.resetAutoIncrementColumns(applicationContext, "user_entity");
        File dataJson = Paths.get("src", "test", "resources", "users.json").toFile();
        UserEntity[] users = new ObjectMapper().readValue(dataJson, UserEntity[].class);
        Arrays.stream(users).forEach(usersRepository::save);
    }

    @Test
    @DisplayName("User not found with non-existing email")
    public void testUserNotFoundWithNonExistingEmail() {
        //Given
        //When
        Optional<UserEntity> userEntityOptional = usersRepository.findByEmail("non-existent");
        assertThat(userEntityOptional.isPresent()).isFalse();
    }

    @Test
    @DisplayName("User found with existing email")
    public void testUserFoundWithExistingEmail() {
        //Given
        //When
        Optional<UserEntity> userEntityOptional = usersRepository.findByEmail("testEmail1");
        //Then
        assertThat(userEntityOptional.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Delete existing user")
    @Transactional
    public void testDeleteExistingUser() {
        //Given
        //When
        usersRepository.deleteByEmail("testEmail1");
        int sizeAfterDelete = usersRepository.findAll().size();
        //Then
        assertThat(sizeAfterDelete).isEqualTo(1);
    }

    @Test
    @DisplayName("Adding new user")
    public void testAddingNewUser() {
        //Given
        FormUserTemplateDTO newUser = FormUserTemplateDTO.builder()
                .name("newUser")
                .email("newUser")
                .password("Password")
                .build();
        UserEntity expectedUser = UserEntity.apply(newUser);
        //When
        UserEntity savedUser = usersRepository.save(expectedUser);
        //Then
        assertThat(savedUser.getName()).isEqualTo(expectedUser.getName());
        assertThat(savedUser.getEmail()).isEqualTo(expectedUser.getEmail());
        List<UserEntity> allUsers = usersRepository.findAll();
        int size = allUsers.size();
        assertThat(size).isEqualTo(3);
    }

    @Test
    @DisplayName("Update existing user")
    public void testUpdateExistingUser() {
        //Given
        FormUserTemplateDTO editedUser = FormUserTemplateDTO.builder()
                .id(1L)
                .name("editedUser")
                .email("testEmail1")
                .password("editedPassword")
                .build();
        UserEntity expectedUser = UserEntity.apply(editedUser);
        //When
        UserEntity savedUser = usersRepository.save(expectedUser);
        //Then
        assertThat(savedUser.getName()).isEqualTo(expectedUser.getName());
        assertThat(savedUser.getEmail()).isEqualTo(expectedUser.getEmail());
        List<UserEntity> allUsers = usersRepository.findAll();
        int size = allUsers.size();
        assertThat(size).isEqualTo(2);
    }
}
