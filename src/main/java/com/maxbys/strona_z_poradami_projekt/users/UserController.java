package com.maxbys.strona_z_poradami_projekt.users;

import com.maxbys.strona_z_poradami_projekt.questions.Question;
import com.maxbys.strona_z_poradami_projekt.questions.QuestionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final QuestionsService questionsService;

    @Autowired
    public UserController(UsersService usersService, QuestionsService questionsService) {
        this.usersService = usersService;
        this.questionsService = questionsService;
    }

    @GetMapping("/users")
    public String goToListOfAllUsersPage(Model model) {
        List<User> listOfAllUsers = usersService.findAll();
        model.addAttribute("allUsers", listOfAllUsers);
        return "usersList";
    }

    //todo musze zmienić formularz w register żeby miał th:object
    @GetMapping("/register")
    public String goToRegisterPage(Model model) {
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
        User newUser = new User();
        newUser.setName(userData.getName());
        newUser.setEmail(userData.getEmail());
        newUser.setPassword(userData.getPassword());
        usersService.save(newUser);
        return "/login";
    }

    private void checkUserParams(FormUserTemplate userData, List<String> errorMsg) {
        if(usersService.findByEmail(userData.getEmail()).isPresent()){
            errorMsg.add("There already is user with this email");
        }
        if(!userData.getName().matches("[a-zA-Z\\d]{6,16}")){
            errorMsg.add("Nickname can't have special characters and must have between 6 and 16 characters");
        }
        if(!userData.getEmail().matches(".+@.+\\..+")){
            errorMsg.add("Wrong email form");
        }
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
        String username = principal.getName();
        Optional<User> userOptional = usersService.findByEmail(username);
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("No user with this nickname"));
        FormUserTemplate formUserTemplate = new FormUserTemplate();
        formUserTemplate.setName(user.getName());
        model.addAttribute("formUser", formUserTemplate);
        return "edit-profile";
    }


    @PostMapping("/profile/edit")
    public RedirectView editProfile(HttpServletRequest request, Principal principal, RedirectAttributes redirectAttributes, @ModelAttribute FormUserTemplate editedUserData) {
        String username = principal.getName();
        Optional<User> userOptional = usersService.findByEmail(username);
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("No user with this nickname"));
        List<String> errorMsgs = new ArrayList<>();
        checkEditedUserParams(editedUserData, errorMsgs);
        if(!errorMsgs.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsgs", errorMsgs);
            return new RedirectView("/profile/edit");
        }
        request.setAttribute(
                View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        user.setName(editedUserData.getName());
        if(!(editedUserData.getPassword().isEmpty() && editedUserData.getPassword_repeated().isEmpty())) {
            user.setPassword(editedUserData.getPassword());
            usersService.save(user);
            return new RedirectView("/logout");
        }
        usersService.saveWithoutEncoding(user);
        return new RedirectView("/logout");
    }

    private void checkEditedUserParams(FormUserTemplate userData, List<String> errorMsg) {
        if(!userData.getName().matches("[a-zA-Z\\d]{6,16}")){
            errorMsg.add("Nickname can't have special characters and must have between 6 and 16 characters");
        }
        if(!userData.getPassword().equals(userData.getPassword_repeated())){
            errorMsg.add("Repeated password doesn't match first password");
        }
        if(!userData.getPassword().matches("((?=.*[\\d])(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\s]).{8,}|)")){
            errorMsg.add("Password must have 8 letters, at least one uppercase," +
                    " at least one lowercase," +
                    " at least one digit and at least one special sign");
        }
    }





    //todo wywalić to wszystko na dole


    //    @GetMapping("users/{name}")
//    public String goToUserPage(@PathVariable String name, Model model){
//        Optional<User> optionalUser = usersService.findByName(name);
//        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("No user found with this username."));
//        model.addAttribute("user", user);
//        return "userViewPage";
//
//    }


//    @ResponseBody
//    @GetMapping("/rolo")
//    public Set<Question> rolo(){
//        return usersService.getQuestionsForUserByName("rolo");
//    }

//    @ResponseBody
//    @GetMapping("/rol")
//    public String rol() throws InterruptedException {
//        //start sesji
//        Optional<User> rolo = usersService.findByName("rolo");
//        //koniec sesji?
//        User user = rolo.get();
//
//        //tutaj dobieram siędo elementu @OneToMany czyli domyślnie lazy a mimo wszystko drukuje bez problemu
//        return user.getQuestions().toString();
//    }
}
