package com.maxbys.page_with_tips_project.users;

import com.maxbys.page_with_tips_project.forgotten_password.FormPasswordChange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
class UsersServiceTest {

    private UsersService usersService;
    @Mock
    private UsersRepository usersRepository;

    @BeforeEach
    public void setUp() {
        usersService = new UsersService(usersRepository, new BCryptPasswordEncoder(10));
    }

    @Test
    @DisplayName("Throw error when looking for user with not existing email")
    public void testUserNotFoundWithNonExistingEmail() {
        //Given
        when(usersRepository.findByEmail("non-existing")).thenReturn(Optional.empty());
        //When
        //Then
        assertThatThrownBy(() -> usersService.findByEmail("non-existing"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User with email non-existing doesn't exist");
    }

    @Test
    @DisplayName("Get user dto object when looking for user with existing email")
    public void testUserFoundWithExistingEmail() {
        //Given
        FormUserTemplateDTO formUserTemplateDTO = FormUserTemplateDTO.builder()
                .name("test")
                .email("testEmail")
                .password("password")
                .build();
        UserEntity userEntity = UserEntity.apply(formUserTemplateDTO);
        when(usersRepository.findByEmail("testEmail")).thenReturn(Optional.of(userEntity));
        UserDTO expectedUserEntity = UserDTO.apply(userEntity);
        //When
        UserDTO returnedUserDTO = usersService.findByEmail("testEmail");
        //Then
        assertThat(returnedUserDTO.getEmail()).isEqualTo(expectedUserEntity.getEmail());
        assertThat(returnedUserDTO.getName()).isEqualTo(expectedUserEntity.getName());
    }

    @Test
    @DisplayName("Enter user repository save method")
    public void testSaveUser() {
        //Given
        FormUserTemplateDTO formUserTemplateDTO = FormUserTemplateDTO.builder()
                .name("test")
                .email("testEmail")
                .password("password")
                .build();
        UserEntity userEntity = UserEntity.apply(formUserTemplateDTO);
        //When
        usersService.save(formUserTemplateDTO);
        //Then
        verify(usersRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("Throw error when editing non-existing user without changing his password")
    public void testThrowErrorWhileEditingVisibleNameOfNonExistingUser() {
        //Given
        when(usersRepository.findByEmail("non-existing")).thenReturn(Optional.empty());
        FormUserTemplateDTO formUserTemplateDTO = FormUserTemplateDTO.builder()
                .email("non-existing")
                .name("editedName")
                .password("samePassword")
                .build();
        //When
        //Then
        assertThatThrownBy(() -> usersService.saveWithoutEncoding(formUserTemplateDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User with email non-existing doesn't exist");
    }

    @Test
    @DisplayName("Enter user repository save method when editing existing user without changing his password")
    public void testEnterUserRepositorySaveMethodWhenEditingExistingUserWithoutChangingHisPassword() {
        //Given
        FormUserTemplateDTO formUserTemplateDTO = FormUserTemplateDTO.builder()
                .email("existing")
                .name("editedName")
                .password("samePassword")
                .build();
        UserEntity userEntity = UserEntity.apply(formUserTemplateDTO);
        String email = formUserTemplateDTO.getEmail();
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        //When
        usersService.saveWithoutEncoding(formUserTemplateDTO);
        //Then
        verify(usersRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("Throw error when loading user details of non existing user")
    public void testThrowErrorWhenLoadingUserDetailsOfNonExistingUser() {
        //Given
        String email = "non-existing";
        when(usersRepository.findByEmail(email)).thenReturn(Optional.empty());
        //When
        //Then
        assertThatThrownBy(() -> usersService.loadUserByUsername(email))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("There is no user with this email.");
    }

    @Test
    @DisplayName("Load user details of existing user")
    public void testLoadUserDetailsOfExistingUser() {
        //Given
        FormUserTemplateDTO formUserTemplateDTO = FormUserTemplateDTO.builder()
                .name("user")
                .email("existing")
                .password("password")
                .build();
        UserEntity userEntity = UserEntity.apply(formUserTemplateDTO);
        String email = formUserTemplateDTO.getEmail();
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        //When
        //Then
        assertThat(usersService.loadUserByUsername(email)).isEqualTo(userEntity);
    }

    @Test
    @DisplayName("Throw error when editing non-existing user's password")
    public void testThrowErrorWhenEditingNonExistingUsersPassword() {
        //Given
        String email = "non-existing";
        when(usersRepository.findByEmail(email)).thenReturn(Optional.empty());
        FormPasswordChange formPasswordChange = FormPasswordChange.builder()
                .password("editedPassword")
                .build();
        //When
        //Then
        assertThatThrownBy(() -> usersService.changePasswordOfUser(formPasswordChange, email))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User with email non-existing doesn't exist");
    }

    @Test
    @DisplayName("Enter user repository save method when editing existing user's password")
    public void testEnterUserRepositorySaveMethodWhenEditingExistingUsersPassword() {
        //Given
        FormPasswordChange formPasswordChange = FormPasswordChange.builder()
                .password("editedPassword")
                .build();
        FormUserTemplateDTO formUserTemplateDTO = FormUserTemplateDTO.builder()
                .email("existing")
                .name("editedName")
                .password("samePassword")
                .build();
        String email = formUserTemplateDTO.getEmail();
        UserEntity userEntity = UserEntity.apply(formUserTemplateDTO);
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        //When
        usersService.changePasswordOfUser(formPasswordChange, email);
        //Then
        verify(usersRepository, times(1)).save(userEntity);
    }
}
