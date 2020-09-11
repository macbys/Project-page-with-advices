package com.maxbys.strona_z_poradami_projekt.forgotten_password;

import com.maxbys.strona_z_poradami_projekt.users.User;
import com.maxbys.strona_z_poradami_projekt.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ForgotPasswordController {

    private MailService mailService;
    private UsersService usersService;
    private ForgottenPasswordTokenService forgottenPasswordTokenService;

    @Autowired
    public ForgotPasswordController(MailService mailService, UsersService usersService, ForgottenPasswordTokenService forgottenPasswordTokenService) {
        this.mailService = mailService;
        this.usersService = usersService;
        this.forgottenPasswordTokenService = forgottenPasswordTokenService;
    }

    @GetMapping("/forgot-password")
    public String goToForgotPasswordForm(Model model) {
        model.addAttribute("user", new User());
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String getDataFromForgotPasswordForm(Model model, @ModelAttribute User emailForm) {
        String email = emailForm.getEmail();
        Optional<User> optionalUser = usersService.findByEmail(email);
        if(!optionalUser.isPresent()) {
            return goToForgotPasswordPageWithError(model);
        }
        String randomPathEnding = saveForgottenPasswordToken(email);
        boolean canSendMail = checkIfCanSendMail(email, randomPathEnding);
        if (canSendMail) {
            return goToErrorPage(model, "There are some problems with our mailing service, we are sorry.");
        }
        return "successfully-sent-password";
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

    private String saveForgottenPasswordToken(String email) {
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(10);
        String randomPathEnding = UUID.randomUUID().toString();
        ForgottenPasswordToken forgottenPasswordToken = ForgottenPasswordToken.builder()
                .email(email)
                .expiryDate(expiryDate)
                .randomPathEnding(randomPathEnding)
                .build();
        forgottenPasswordTokenService.save(forgottenPasswordToken);
        return randomPathEnding;
    }

    private String goToErrorPage(Model model, String errorMsg) {
        model.addAttribute("errorMsg", errorMsg);
        return "error-page";
    }

    private String goToForgotPasswordPageWithError(Model model) {
        String wrongEmailMsg = "There is no user with this email";
        model.addAttribute("errorMsg", wrongEmailMsg);
        return goToForgotPasswordForm(model);
    }

    private String generateMessage(String randomPathEnding, String email) {
        String link = "http://localhost:8080/change-password/" + randomPathEnding + "?email=" + email;
        return "<p>To change your password go to link below:</p><br>" +
                "<a href='" + link + "'>" + link + "</a>";
    }

    @GetMapping("/change-password/{randomPathEnding}")
    private String changePasswordPage(Model model, @PathVariable String randomPathEnding,
                                      @RequestParam String email) {
        model.addAttribute("passwordChangeForm", new FormPasswordChange());
        return "change-password";
    }

    @PostMapping("/change-password/{randomPathEnding}")
    public String changePassword(Model model, @PathVariable String randomPathEnding, @RequestParam String email
            , @ModelAttribute FormPasswordChange formPasswordChange) {
        Optional<ForgottenPasswordToken> forgottenPasswordTokenOptional = forgottenPasswordTokenService.findById(email);
        String errorMsg = checkToken(forgottenPasswordTokenOptional, randomPathEnding);
        if(errorMsg != null) {
            goToErrorPage(model, errorMsg);
        }
        String wrongPasswordMsg = checkPassword(formPasswordChange);
        if(wrongPasswordMsg != null) {
            model.addAttribute("wrongPasswordMsg", wrongPasswordMsg);
            return changePasswordPage(model, randomPathEnding, email);
        }
        saveUserWithChangedPassword(email, formPasswordChange);
        return goBackToHomePage(forgottenPasswordTokenOptional);
    }

    private String checkToken(Optional<ForgottenPasswordToken> forgottenPasswordTokenOptional, String randomPathEnding) {
        if(!forgottenPasswordTokenOptional.isPresent()) {
            return "There is no password change link for this email";
        }
        ForgottenPasswordToken forgottenPasswordToken = forgottenPasswordTokenOptional.get();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = forgottenPasswordToken.getExpiryDate();
        if(now.isAfter(expiryDate)){
            return "This link is expired";
        }
        String trueRandomPathEnding = forgottenPasswordToken.getRandomPathEnding();
        if(!randomPathEnding.equals(trueRandomPathEnding)) {
            return "Invalid link";
        }
        return null;
    }

    private String checkPassword(FormPasswordChange formPasswordChange) {
        String password = formPasswordChange.getPassword();
        String passwordRepeated = formPasswordChange.getPasswordRepeated();
        if(!password.equals(passwordRepeated)) {
            return "Repeated password doesn't match original";
        }
        if(!password.matches("(?=.*[\\d])(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\s]).{8,}")) {
            return "Password must have 8 letters, at least one uppercase," +
                    " at least one lowercase," +
                    " at least one digit and at least one special sign";
        }
        return null;
    }

    private void saveUserWithChangedPassword(@RequestParam String email, @ModelAttribute FormPasswordChange formPasswordChange) {
        Optional<User> userOptional = usersService.findByEmail(email);
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setPassword(formPasswordChange.getPassword());
        usersService.save(user);
    }

    private String goBackToHomePage(Optional<ForgottenPasswordToken> forgottenPasswordTokenOptional) {
        settingForgottenPasswordTokenToExpired(forgottenPasswordTokenOptional);
        return "redirect:/";
    }

    private void settingForgottenPasswordTokenToExpired(Optional<ForgottenPasswordToken> forgottenPasswordTokenOptional) {
        ForgottenPasswordToken forgottenPasswordToken = forgottenPasswordTokenOptional.get();
        forgottenPasswordToken.setExpiryDate(LocalDateTime.now());
        forgottenPasswordTokenService.save(forgottenPasswordToken);
    }
}
