package com.maxbys.strona_z_poradami_projekt.users;

import com.maxbys.strona_z_poradami_projekt.forgotten_password.FormPasswordChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsersService implements UserDetailsService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public UserDTO findByEmail(String email){
        Optional<UserEntity> userEntityOptional = usersRepository.findByEmail(email);
        UserEntity userEntity = userEntityOptional.orElseThrow(() -> new RuntimeException());
        UserDTO userDTO = UserDTO.apply(userEntity);
        return userDTO;
    }

    public void save(FormUserTemplateDTO formUserTemplateDTO) {
        formUserTemplateDTO.setPassword(passwordEncoder.encode(formUserTemplateDTO.getPassword()));
        UserEntity userEntity = UserEntity.apply(formUserTemplateDTO);
        usersRepository.save(userEntity);
    }

    public void saveWithoutEncoding(FormUserTemplateDTO formUserTemplateDTO) {
        Optional<UserEntity> userEntityOptional = usersRepository.findByEmail(formUserTemplateDTO.getEmail());
        UserEntity userEntity = userEntityOptional.orElseThrow(() -> new RuntimeException());
        userEntity = UserEntity.updateWithoutPassword(userEntity, formUserTemplateDTO);
        usersRepository.save(userEntity);
    }


    public void deleteByName(String email){
        usersRepository.deleteByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser = usersRepository.findByEmail(email);
        return optionalUser
                .orElseThrow(() ->new UsernameNotFoundException("There is no user with this email."));
    }

    public void changePasswordOfUser(FormPasswordChange formPasswordChange, String email) {
        Optional<UserEntity> userEntityOptional = usersRepository.findByEmail(email);
        UserEntity userEntity = userEntityOptional.orElseThrow(() ->
                new RuntimeException("User with email " + email + " doesn't exist"));
        String encodedPassword = passwordEncoder.encode(formPasswordChange.getPassword());
        formPasswordChange.setPassword(encodedPassword);
        UserEntity.changeUserPassword(userEntity, formPasswordChange);
        usersRepository.save(userEntity);
    }
}
