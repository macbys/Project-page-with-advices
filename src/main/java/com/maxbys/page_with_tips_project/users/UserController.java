package com.maxbys.page_with_tips_project.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    public String postRegisterData(Model model, @ModelAttribute FormUserTemplateDTO userData) throws IOException {
        userData.setRole("USER");
        userData.setId(null);
        List<String> errorMsgs = new ArrayList<>();
        checkIfAvatarWasChosen(userData);
        checkUserParams(userData, errorMsgs);
        if(!errorMsgs.isEmpty()) {
            model.addAttribute("errorMsgs", errorMsgs);
            return goToRegisterPage(model);
        }
        usersService.save(userData);
        return "/login";
    }

    private void checkIfAvatarWasChosen(@ModelAttribute FormUserTemplateDTO userData) throws IOException {
        if (userData.getAvatar().getBytes().length < 2) {
            userData.setAvatar(null);
        }
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
        String avatar = userDTO.getAvatar();
        model.addAttribute("avatar", avatar);
        Long id = userDTO.getId();
        formUserTemplateDTO.setId(id);
        model.addAttribute("formUser", formUserTemplateDTO);
        return "edit-profile";
    }

    private UserDTO getUser(Principal principal) {
        String principalName = principal.getName();
        UserDTO userOptional = usersService.findByEmail(principalName);
        return userOptional;
    }

    @PostMapping("/profile/edit")
    public RedirectView editProfile(HttpServletRequest request, Principal principal, RedirectAttributes redirectAttributes, @ModelAttribute FormUserTemplateDTO editedUserData) throws IOException {
        UserDTO userDTO = getUser(principal);
        editedUserData.setEmail(userDTO.getEmail());
        checkIfAvatarWasChosen(editedUserData);
        boolean isEditProfileFormInvalid = checkFormForErrors(redirectAttributes, editedUserData);
        if (isEditProfileFormInvalid) {
            return new RedirectView("/profile/edit");
        }
        boolean isPasswordEdited = editProfileWithPassword(editedUserData, userDTO);
        if (isPasswordEdited) {
            request.setAttribute(
                    View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
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
        if(!userData.getPassword().isEmpty()) {
            validatingPassword(userData, errorMsg);
        }
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
        return new RedirectView("/");
    }
    
    @GetMapping("/users-ranking")
    public String goToPageWithRankedUsers(Model model) {
        Page<UserWithPointsDTO> ranking = usersService.getRanking();
        model.addAttribute("rankedUsers", ranking);
        return "users-ranking";
    }

    @GetMapping("/user/{userId}")
    public String goToUserProfilePage(Model model, @PathVariable Long userId) {
        UserDTO userDTO = usersService.findById(userId);
        model.addAttribute("userDTO", userDTO);
        UserWithPointsDTO usersPointsAndRanking = usersService.getUsersPointsAndRanking(userId);
        model.addAttribute("usersPointsAndRanking", usersPointsAndRanking);
        return "profile-page";
    }

    @PostMapping("/user/{userId}/delete")
    public RedirectView deleteUser(@PathVariable Long userId, Authentication authentication) {
        UserDTO userDTO = usersService.findById(userId);
        boolean isUserAllowedToDeleteThatUser = isLoggedUserAdminOrUserBeingDeleted(authentication, userDTO);
        if (isUserAllowedToDeleteThatUser){
            usersService.deleteById(userId);
            return new RedirectView("/");
        }
        throw new RuntimeException("User with email " + authentication.getName() + " isn't allowed to delete user with " + userId + " id");
    }

    @PostMapping("/profile/delete")
    public RedirectView deleteLoggedUser(Principal principal, HttpServletRequest request) {
        usersService.deleteUserByEmail(principal.getName());
        request.setAttribute(
                View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return new RedirectView("/logout");
    }

    private boolean isLoggedUserAdminOrUserBeingDeleted(Authentication authentication, UserDTO userDTO) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")) || userDTO.getEmail().equals(authentication.getName());
    }
}
