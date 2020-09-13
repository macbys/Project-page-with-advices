package com.maxbys.strona_z_poradami_projekt;

import com.maxbys.strona_z_poradami_projekt.answers.AnswerDTO;
import com.maxbys.strona_z_poradami_projekt.answers.AnswersService;
import com.maxbys.strona_z_poradami_projekt.categories.CategoriesService;
import com.maxbys.strona_z_poradami_projekt.categories.CategoryDTO;
import com.maxbys.strona_z_poradami_projekt.comments.CommentDTO;
import com.maxbys.strona_z_poradami_projekt.comments.CommentsService;
import com.maxbys.strona_z_poradami_projekt.questions.FormQuestionTemplate;
import com.maxbys.strona_z_poradami_projekt.questions.QuestionDTO;
import com.maxbys.strona_z_poradami_projekt.questions.QuestionsService;
import com.maxbys.strona_z_poradami_projekt.users.FormUserTemplateDTO;
import com.maxbys.strona_z_poradami_projekt.users.UserDTO;
import com.maxbys.strona_z_poradami_projekt.users.UsersService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataSeed implements InitializingBean {

    private final CategoriesService categoriesService;
    private final UsersService usersService;
    private final QuestionsService questionsService;
    private final AnswersService answersService;
    private final CommentsService commentsService;

    @Autowired
    public DataSeed(CategoriesService categoriesService, UsersService usersService, QuestionsService questionsService, AnswersService answersService, CommentsService commentsService) {
        this.categoriesService = categoriesService;
        this.usersService = usersService;
        this.questionsService = questionsService;
        this.answersService = answersService;
        this.commentsService = commentsService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CategoryDTO programingCategory = CategoryDTO.builder()
                .name("Programing")
                .build();
        categoriesService.save(programingCategory);
        CategoryDTO javaCategory = CategoryDTO.builder()
                .superiorCategory(programingCategory)
                .name("Java")
                .build();
        categoriesService.save(javaCategory);
        CategoryDTO foodCategory = CategoryDTO.builder()
                .name("Food")
                .build();
        categoriesService.save(foodCategory);
        FormUserTemplateDTO emptyUser = FormUserTemplateDTO.builder()
                .password("")
                .name("emptyUser")
                .email("")
                .build();
        usersService.save(emptyUser);
        FormUserTemplateDTO roloUser = FormUserTemplateDTO.builder()
                .name("rolo")
                .email("maxbys@gmail.com")
                .password("redo")
                .build();
        usersService.save(roloUser);
        UserDTO roloUserDTO = UserDTO.builder()
                .email(roloUser.getEmail())
                .name(roloUser.getName())
                .build();
        FormQuestionTemplate fibonacciQuestion = FormQuestionTemplate.builder()
                .category(javaCategory.getName())
                .questionValue("How count fibonacci code in java?")
                .build();
        questionsService.save(fibonacciQuestion, roloUserDTO);
        QuestionDTO fibonacciQuestionDTO = questionsService.findById(1L);
        UserDTO emptyUserDTO = UserDTO.builder()
                .email(emptyUser.getEmail())
                .name(emptyUser.getName())
                .build();
        AnswerDTO fibonacciAnswer1 = AnswerDTO.builder()
                .questionDTO(fibonacciQuestionDTO)
                .rating(25)
                .value("public int fibonacciRecursive(int number) {\n" +
                        "        if (number <= 1) {\n" +
                        "            return 0;\n" +
                        "        }\n" +
                        "        if (number == 2 || number == 3) {\n" +
                        "            return 1;\n" +
                        "        }\n" +
                        "        return fibonacciRecursive(number - 2) + fibonacciRecursive(number - 1);\n" +
                        "    }")
                .userDTO(emptyUserDTO)
                .build();
        answersService.save(fibonacciAnswer1);
        FormUserTemplateDTO gwenUser = FormUserTemplateDTO.builder()
                .password("Hdsfw32ssdfga")
                .name("GwennewG")
                .email("gwen")
                .build();
        usersService.save(gwenUser);
        UserDTO gwenUserDTO = UserDTO.builder()
                .email(gwenUser.getEmail())
                .name(gwenUser.getName())
                .build();
        AnswerDTO fibonacciAnswer2 = AnswerDTO.builder()
                .userDTO(gwenUserDTO)
                .rating(40)
                .value("public class Fibonacci {\n" +
                        "\n" +
                        "    public static void main(String[] args) {\n" +
                        "        IntStream stream = IntStream.generate(new FibonacciSupplier());\n" +
                        "        stream.limit(20).forEach(System.out::println);\n" +
                        "    }\n" +
                        "\n" +
                        "    private static class FibonacciSupplier implements IntSupplier {\n" +
                        "\n" +
                        "        int current = 1;\n" +
                        "        int previous = 0;\n" +
                        "\n" +
                        "        @Override\n" +
                        "        public int getAsInt() {\n" +
                        "            int result = current;\n" +
                        "            current = previous + current;\n" +
                        "            previous = result;\n" +
                        "            return result;\n" +
                        "        }\n" +
                        "    }\n" +
                        "}")
                .questionDTO(fibonacciQuestionDTO)
                .build();
        answersService.save(fibonacciAnswer2);
        CommentDTO fibonacciComment = CommentDTO.builder()
                .value("Thank you.")
                .build();
        commentsService.save(fibonacciComment, 2L, roloUser.getEmail());
    }


}
