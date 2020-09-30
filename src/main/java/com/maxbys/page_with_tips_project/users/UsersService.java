package com.maxbys.page_with_tips_project.users;

import com.maxbys.page_with_tips_project.forgotten_password.FormPasswordChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsersService implements UserDetailsService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;

    }

    @Transactional
    public UserDTO findById(Long id) {
        Optional<UserEntity> userEntityOptional = usersRepository.findById(id);
        UserEntity userEntity = userEntityOptional.orElseThrow(() ->
                new RuntimeException("User with id " + id + " doesn't exist"));
        UserDTO userDTO = UserDTO.apply(userEntity);
        return userDTO;
    }

    @Transactional
    public UserDTO findByEmail(String email) {
        Optional<UserEntity> userEntityOptional = usersRepository.findByEmail(email);
        UserEntity userEntity = userEntityOptional.orElseThrow(() ->
                new RuntimeException("User with email " + email + " doesn't exist"));
        UserDTO userDTO = UserDTO.apply(userEntity);
        return userDTO;
    }

    public void save(FormUserTemplateDTO formUserTemplateDTO) {
        formUserTemplateDTO.setId(null);
        formUserTemplateDTO.setPassword(passwordEncoder.encode(formUserTemplateDTO.getPassword()));
        UserEntity userEntity = UserEntity.apply(formUserTemplateDTO);
        Optional<UserEntity> existingUserOptional = usersRepository.findByEmail(formUserTemplateDTO.getEmail());
        boolean editingExistingUser = existingUserOptional.isPresent();
        if(formUserTemplateDTO.getAvatar() == null && editingExistingUser) {
            UserEntity existingUser = existingUserOptional.get();
            if(existingUser.getAvatar() != null) {
                userEntity.setAvatar(existingUser.getAvatar());
            }
        }
        usersRepository.save(userEntity);
    }

    public void saveWithoutEncoding(FormUserTemplateDTO formUserTemplateDTO) {
        String email = formUserTemplateDTO.getEmail();
        Optional<UserEntity> userEntityOptional = usersRepository.findByEmail(email);
        UserEntity userEntity = userEntityOptional.orElseThrow(() ->
                new RuntimeException("User with email " + email + " doesn't exist"));
        userEntity = formUserTemplateDTO.getAvatar() != null?
                UserEntity.updateWithoutPassword(userEntity, formUserTemplateDTO) :
                UserEntity.updateWithoutPasswordAndAvatar(userEntity, formUserTemplateDTO);
        usersRepository.save(userEntity);
    }

    public void deleteById(Long userId){
        usersRepository.deleteById(userId);
    }

    @Transactional
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

    public Page<UserWithPointsDTO> getRanking() {
        List<Object[]> ranking = usersRepository.getRanking();
        List<UserWithPointsDTO> userWithPointsDTOList = ranking.stream()
                .map(objects -> {
                    BigInteger idBigInteger = (BigInteger) objects[0];
                    Long id = Long.valueOf(idBigInteger.toString());
                    UserDTO userDTO = new UserDTO(id, (String) objects[2], (String) objects[1], null, null);
                    return UserWithPointsDTO.builder()
                            .userDTO(userDTO)
                            .points((BigInteger) objects[3])
                            .rank((BigInteger) objects[4])
                            .build();
                })
                .collect(Collectors.toList());
        PageImpl<UserWithPointsDTO> rankedUsersPage = new PageImpl<>(userWithPointsDTOList, PageRequest.of(0, 100), userWithPointsDTOList.size());
        return rankedUsersPage;
    }

    public UserWithPointsDTO getUsersPointsAndRanking(Long id) {
        Object[] objects = usersRepository.getUsersPointsAndRanking(id);
        Object[] object = (Object[]) objects[0];
        UserWithPointsDTO userWithPointsDTO = UserWithPointsDTO.builder()
                .points((BigInteger) object[1])
                .build();
        return userWithPointsDTO;
    }

    @Transactional
    public void deleteUserByEmail(String email) {
        usersRepository.deleteByEmail(email);
    }
}
