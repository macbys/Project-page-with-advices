package com.maxbys.strona_z_poradami_projekt.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

@Controller
public class UserController {

    private final UsersService usersService;

    @Autowired
    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/register")
    public String goToRegisterPage(Model model) {
        FormUserTemplate formUserTemplate = new FormUserTemplate();
        model.addAttribute("formUser", formUserTemplate);
        return "register";
    }

    @PostMapping("/register")
    public String postRegisterData(Model model, @ModelAttribute FormUserTemplate userData) {
        List<String> errorMsgs = new ArrayList<>();
        checkUserParams(userData, errorMsgs);
        if(!errorMsgs.isEmpty()) {
            model.addAttribute("errorMsgs", errorMsgs);
            return goToRegisterPage(model);
        }
        saveUserIntoRepository(userData);
        return "/login";
    }

    private void checkUserParams(FormUserTemplate userData, List<String> errorMsg) {
        validatingEmail(userData, errorMsg);
        validatingVisibleName(userData, errorMsg);
        validatingPassword(userData, errorMsg);
    }

    private void validatingEmail(FormUserTemplate userData, List<String> errorMsg) {
        if(usersService.findByEmail(userData.getEmail()).isPresent()){
            errorMsg.add("There already is user with this email");
        }
        if(!userData.getEmail().matches(".+@.+\\..+")){
            errorMsg.add("Wrong email form");
        }
    }

    private void validatingVisibleName(FormUserTemplate userData, List<String> errorMsg) {
        if(!userData.getName().matches("[a-zA-Z\\d]{6,16}")){
            errorMsg.add("Nickname can't have special characters and must have between 6 and 16 characters");
        }
    }

    private void validatingPassword(FormUserTemplate userData, List<String> errorMsg) {
        if(!userData.getPassword().equals(userData.getPassword_repeated())){
            errorMsg.add("Repeated password doesn't match first password");
        }
        if(!userData.getPassword().matches("(?=.*[\\d])(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\s]).{8,}")){
            errorMsg.add("Password must have 8 letters, at least one uppercase," +
                    " at least one lowercase," +
                    " at least one digit and at least one special sign");
        }
    }

    private void saveUserIntoRepository(@ModelAttribute FormUserTemplate userData) {
        User newUser = User.builder()
                .name(userData.getName())
                .email(userData.getEmail())
                .password(userData.getPassword())
                .build();
        usersService.save(newUser);
    }

    @GetMapping("/profile/edit")
    public String goToEditProfilePage(Principal principal, Model model) {
        User user = getUser(principal);
        FormUserTemplate formUserTemplate = new FormUserTemplate();
        formUserTemplate.setName(user.getName());
        model.addAttribute("formUser", formUserTemplate);
        return "edit-profile";
    }

    private User getUser(Principal principal) {
        String principalName = principal.getName();
        Optional<User> userOptional = usersService.findByEmail(principalName);
        return userOptional.orElseThrow(() -> new RuntimeException("User with email " + principalName + "doesn't exist"));
    }

    @PostMapping("/profile/edit")
    public RedirectView editProfile(HttpServletRequest request, Principal principal, RedirectAttributes redirectAttributes, @ModelAttribute FormUserTemplate editedUserData) {
        User user = getUser(principal);
        boolean isEditProfileFormInvalid = checkFormForErrors(redirectAttributes, editedUserData);
        if (isEditProfileFormInvalid) {
            return new RedirectView("/profile/edit");
        }
        request.setAttribute(
                View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        boolean isPasswordEdited = editProfileWithPassword(editedUserData, user);
        if (isPasswordEdited) {
            return new RedirectView("/logout");
        }
        return editProfileWithoutPassword(user);
    }

    private boolean checkFormForErrors(RedirectAttributes redirectAttributes, @ModelAttribute FormUserTemplate editedUserData) {
        List<String> errorMsgs = new ArrayList<>();
        checkEditedUserParams(editedUserData, errorMsgs);
        if(!errorMsgs.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsgs", errorMsgs);
            return true;
        }
        return false;
    }

    private void checkEditedUserParams(FormUserTemplate userData, List<String> errorMsg) {
        validatingVisibleName(userData, errorMsg);
        validatingPassword(userData, errorMsg);
    }

    private boolean editProfileWithPassword(@ModelAttribute FormUserTemplate editedUserData, User user) {
        user.setName(editedUserData.getName());
        if(!(editedUserData.getPassword().isEmpty() && editedUserData.getPassword_repeated().isEmpty())) {
            user.setPassword(editedUserData.getPassword());
            usersService.save(user);
            return true;
        }
        return false;
    }

    private RedirectView editProfileWithoutPassword(User user) {
        usersService.saveWithoutEncoding(user);
        return new RedirectView("/logout");
    }
}
