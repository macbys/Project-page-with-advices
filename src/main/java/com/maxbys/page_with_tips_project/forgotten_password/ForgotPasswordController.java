package com.maxbys.page_with_tips_project.forgotten_password;

import com.maxbys.page_with_tips_project.users.UserDTO;
import com.maxbys.page_with_tips_project.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ForgotPasswordController {

    private UsersService usersService;
    private ForgottenPasswordTokenService forgottenPasswordTokenService;

    @Autowired
    public ForgotPasswordController(UsersService usersService, ForgottenPasswordTokenService forgottenPasswordTokenService) {
        this.usersService = usersService;
        this.forgottenPasswordTokenService = forgottenPasswordTokenService;
    }

    @GetMapping("/forgot-password")
    public String goToForgotPasswordForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String getDataFromForgotPasswordForm(Model model, @ModelAttribute UserDTO emailForm) {
        String email = emailForm.getEmail();
        UserDTO userDTO = null;
        try {
            userDTO = usersService.findByEmail(email);
        } catch (RuntimeException ex) {
        }
        if(userDTO == null) {
            return goToForgotPasswordPageWithError(model);
        }
        boolean canSendMail = forgottenPasswordTokenService.sendResetLink(email);
        if (canSendMail) {
            return goToErrorPage(model, "There are some problems with our mailing service, we are sorry.");
        }
        return "successfully-sent-password";
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

    @GetMapping("/change-password/{randomPathEnding}")
    private String changePasswordPage(Model model, @PathVariable String randomPathEnding,
                                      @RequestParam String email) {
        model.addAttribute("passwordChangeForm", new FormPasswordChange());
        return "change-password";
    }

    @PostMapping("/change-password/{randomPathEnding}")
    public String changePassword(Model model, @PathVariable String randomPathEnding, @RequestParam String email
            , @ModelAttribute FormPasswordChange formPasswordChange) {
        String errorMsg = forgottenPasswordTokenService.checkTokenValidity(email, randomPathEnding);
        if(errorMsg != null) {
            return goToErrorPage(model, errorMsg);
        }
        String wrongPasswordMsg = checkPassword(formPasswordChange);
        if(wrongPasswordMsg != null) {
            model.addAttribute("wrongPasswordMsg", wrongPasswordMsg);
            return changePasswordPage(model, randomPathEnding, email);
        }
        return goBackToHomePage(email, formPasswordChange);
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

    private String goBackToHomePage(String email, FormPasswordChange formPasswordChange) {
        usersService.changePasswordOfUser(formPasswordChange, email);
        forgottenPasswordTokenService.setTokenExpiryDateToNow(email);
        return "redirect:/";
    }
}
