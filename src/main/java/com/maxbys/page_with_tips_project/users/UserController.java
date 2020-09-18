package com.maxbys.page_with_tips_project.users;

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
        FormUserTemplateDTO formUserTemplateDTO = new FormUserTemplateDTO();
        model.addAttribute("formUser", formUserTemplateDTO);
        return "register";
    }

    @PostMapping("/register")
    public String postRegisterData(Model model, @ModelAttribute FormUserTemplateDTO userData) {
        List<String> errorMsgs = new ArrayList<>();
        checkUserParams(userData, errorMsgs);
        if(!errorMsgs.isEmpty()) {
            model.addAttribute("errorMsgs", errorMsgs);
            return goToRegisterPage(model);
        }
        usersService.save(userData);
        return "/login";
    }

    private void checkUserParams(FormUserTemplateDTO userData, List<String> errorMsg) {
        validatingEmail(userData, errorMsg);
        validatingVisibleName(userData, errorMsg);
        validatingPassword(userData, errorMsg);
    }

    private void validatingEmail(FormUserTemplateDTO userData, List<String> errorMsg) {
        UserDTO userDTO = null;
        try {
            userDTO = usersService.findByEmail(userData.getEmail());
        } catch(RuntimeException ex) {
        }
        if(userDTO != null) {
            errorMsg.add("User with email " + userData.getEmail() + " already exist");
        }
        if(!userData.getEmail().matches(".+@.+\\..+")){
            errorMsg.add("Wrong email form");
        }
    }

    private void validatingVisibleName(FormUserTemplateDTO userData, List<String> errorMsg) {
        if(!userData.getName().matches("[a-zA-Z\\d]{6,16}")){
            errorMsg.add("Nickname can't have special characters and must have between 6 and 16 characters");
        }
    }

    private void validatingPassword(FormUserTemplateDTO userData, List<String> errorMsg) {
        if(!userData.getPassword().equals(userData.getPassword_repeated())){
            errorMsg.add("Repeated password doesn't match first password");
        }
        if(!userData.getPassword().matches("(?=.*[\\d])(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\s]).{8,}")){
            errorMsg.add("Password must have 8 letters, at least one uppercase," +
                    " at least one lowercase," +
                    " at least one digit and at least one special sign");
        }
    }



    @GetMapping("/profile/edit")
    public String goToEditProfilePage(Principal principal, Model model) {
        UserDTO userDTO = getUser(principal);
        FormUserTemplateDTO formUserTemplateDTO = new FormUserTemplateDTO();
        formUserTemplateDTO.setName(userDTO.getName());
        model.addAttribute("formUser", formUserTemplateDTO);
        return "edit-profile";
    }

    private UserDTO getUser(Principal principal) {
        String principalName = principal.getName();
        UserDTO userOptional = usersService.findByEmail(principalName);
        return userOptional;
    }

    @PostMapping("/profile/edit")
    public RedirectView editProfile(HttpServletRequest request, Principal principal, RedirectAttributes redirectAttributes, @ModelAttribute FormUserTemplateDTO editedUserData) {
        UserDTO userDTO = getUser(principal);
        editedUserData.setEmail(userDTO.getEmail());
        boolean isEditProfileFormInvalid = checkFormForErrors(redirectAttributes, editedUserData);
        if (isEditProfileFormInvalid) {
            return new RedirectView("/profile/edit");
        }
        request.setAttribute(
                View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        boolean isPasswordEdited = editProfileWithPassword(editedUserData, userDTO);
        if (isPasswordEdited) {
            return new RedirectView("/logout");
        }
        return editProfileWithoutPassword(editedUserData);
    }

    private boolean checkFormForErrors(RedirectAttributes redirectAttributes, FormUserTemplateDTO editedUserData) {
        List<String> errorMsgs = new ArrayList<>();
        checkEditedUserParams(editedUserData, errorMsgs);
        if(!errorMsgs.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsgs", errorMsgs);
            return true;
        }
        return false;
    }

    private void checkEditedUserParams(FormUserTemplateDTO userData, List<String> errorMsg) {
        validatingVisibleName(userData, errorMsg);
        validatingPassword(userData, errorMsg);
    }

    private boolean editProfileWithPassword(FormUserTemplateDTO editedUserData, UserDTO userDTO) {
        userDTO.setName(editedUserData.getName());
        if(!(editedUserData.getPassword().isEmpty() && editedUserData.getPassword_repeated().isEmpty())) {
            usersService.save(editedUserData);
            return true;
        }
        return false;
    }

    private RedirectView editProfileWithoutPassword(FormUserTemplateDTO formUserTemplateDTO) {
        usersService.saveWithoutEncoding(formUserTemplateDTO);
        return new RedirectView("/logout");
    }
}
