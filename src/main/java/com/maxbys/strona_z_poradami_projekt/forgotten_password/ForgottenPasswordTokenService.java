package com.maxbys.strona_z_poradami_projekt.forgotten_password;

import com.maxbys.strona_z_poradami_projekt.mail_service.MailService;
import com.maxbys.strona_z_poradami_projekt.users.UserEntity;
import com.maxbys.strona_z_poradami_projekt.users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ForgottenPasswordTokenService {

    private final ForgottenPasswordTokenRepository forgottenPasswordTokenRepository;
    private final UsersRepository usersRepository;
    private final MailService mailService;

    @Autowired
    public ForgottenPasswordTokenService(ForgottenPasswordTokenRepository forgottenPasswordTokenRepository, UsersRepository usersRepository, MailService mailService) {
        this.forgottenPasswordTokenRepository = forgottenPasswordTokenRepository;
        this.usersRepository = usersRepository;
        this.mailService = mailService;
    }

    public boolean sendResetLink(String email) {
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(10);
        String randomPathEnding = UUID.randomUUID().toString();
        Optional<UserEntity> userEntityOptional = usersRepository.findByEmail(email);
        UserEntity userEntity = userEntityOptional.orElseThrow(() ->
                new RuntimeException("User with email " + email + " doesn't exist"));
        ForgottenPasswordToken forgottenPasswordToken = ForgottenPasswordToken.builder()
                .email(new UserEntityEmbedded(userEntity))
                .expiryDate(expiryDate)
                .randomPathEnding(randomPathEnding)
                .build();
        forgottenPasswordTokenRepository.save(forgottenPasswordToken);
        return checkIfCanSendMail(email, randomPathEnding);
    }

    public String checkTokenValidity(String email, String randomPathEnding) {
        Optional<ForgottenPasswordToken> forgottenPasswordTokenOptional = findById(email);
        if(!forgottenPasswordTokenOptional.isPresent()) {
            return "There is no password change link for this email";
        }
        ForgottenPasswordToken forgottenPasswordToken = forgottenPasswordTokenOptional.get();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = forgottenPasswordToken.getExpiryDate();
        if(now.isAfter(expiryDate)) {
            return "This link is expired";
        }
        String trueRandomPathEnding = forgottenPasswordToken.getRandomPathEnding();
        if(!randomPathEnding.equals(trueRandomPathEnding)) {
            return "Invalid link";
        }
        return null;
    }

        public void setTokeExpiryDateToNow(String email) {
        Optional<ForgottenPasswordToken> forgottenPasswordTokenOptional = findById(email);
        ForgottenPasswordToken forgottenPasswordToken = forgottenPasswordTokenOptional.get();
        forgottenPasswordToken.setExpiryDate(LocalDateTime.now());
        forgottenPasswordTokenRepository.save(forgottenPasswordToken);
    }

    private boolean checkIfCanSendMail(String email, String randomPathEnding) {
        try {
            mailService.sendMail(email, "Forgotten password", generateMessage(randomPathEnding, email),
                    true);
        } catch (MessagingException messagingException) {
            return true;
        }
        return false;
    }

    private String generateMessage(String randomPathEnding, String email) {
        String link = "http://localhost:8080/change-password/" + randomPathEnding + "?email=" + email;
        return "<p>To change your password go to link below:</p><br>" +
                "<a href='" + link + "'>" + link + "</a>";
    }

    public Optional<ForgottenPasswordToken> findById(String email) {
        Optional<UserEntity> userEntityOptional = usersRepository.findByEmail(email);
        UserEntity userEntity = userEntityOptional.orElseThrow(() ->
                new RuntimeException("User with email " + email + " doesn't exist"));
        return forgottenPasswordTokenRepository.findById(new UserEntityEmbedded(userEntity));
    }
}
