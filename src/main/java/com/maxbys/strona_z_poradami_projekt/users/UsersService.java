package com.maxbys.strona_z_poradami_projekt.users;
import com.maxbys.strona_z_poradami_projekt.answers.Answer;
import com.maxbys.strona_z_poradami_projekt.comments.Comment;
import com.maxbys.strona_z_poradami_projekt.questions.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.rmi.server.UID;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UsersService implements UserDetailsService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;

    }


    public List<User> findAll(){
        return usersRepository.findAll();
    }

    public Optional<User> findByEmail(String email){
        return usersRepository.findByEmail(email);
    }

    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        usersRepository.save(user);
    }

    public void saveWithoutEncoding(User user) {
        usersRepository.save(user);
    }

    public void deleteByName(String email){
        usersRepository.deleteByEmail(email);
    }

// todo usunac to co niżej
//    public Set<Answer> getAnswersForUserByName(String name){
//        return usersRepository.getAnswersForUserByName(name);
//    }
//
//    public Set<Comment> getCommentsForUserByName(String name){
//        return usersRepository.getCommentsForUserByName(name);
//    }
//
//    public Set<Question> getQuestionsForUserByName(String name){
//        return usersRepository.getQuestionsForUserByName(name);
//    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = usersRepository.findByEmail(email);

        return optionalUser
                .orElseThrow(() ->new UsernameNotFoundException("There is no user with this email."));
    }


}
