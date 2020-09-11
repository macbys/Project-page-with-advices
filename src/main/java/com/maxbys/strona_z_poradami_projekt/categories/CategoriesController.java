package com.maxbys.strona_z_poradami_projekt.categories;

import com.maxbys.strona_z_poradami_projekt.paginagtion.PaginationGenerator;
import com.maxbys.strona_z_poradami_projekt.questions.FormQuestionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;
import java.util.Optional;

@Controller
public class CategoriesController {

    private final CategoriesService categoriesService;

    @Autowired
    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping("/categories")
    public String showAllSuperiorCategories(Model model, Pageable pageable) {
        Page<Category> categoriesWithoutUpperCategory = categoriesService.findAllByCategoryIsNull(pageable);
        model.addAttribute("categories", categoriesWithoutUpperCategory);
        List<Integer> paginationNumbers = PaginationGenerator.createPaginationList(pageable.getPageNumber(), categoriesWithoutUpperCategory.getTotalPages());
        model.addAttribute("paginationNumbers", paginationNumbers);
        return "categories";
    }

    @GetMapping("/categories/{id}")
    public String showSubcategoriesOfCategory(@PathVariable String id, Model model, Pageable pageable) {
        Page<Category> subCategories = categoriesService.findAllByCategoryNameIs(id, pageable);
        model.addAttribute("categories", subCategories);
        List<Integer> paginationNumbers = PaginationGenerator.createPaginationList(pageable.getPageNumber(), subCategories.getTotalPages());
        model.addAttribute("paginationNumbers", paginationNumbers);
        return "categories";
    }

    @PostMapping("/add-category")
    public String addCategory(Model model, @ModelAttribute FormQuestionTemplate formQuestionTemplate) {
        FormQuestionTemplate formQuestionTemplateModel = new FormQuestionTemplate();
        model.addAttribute("question", formQuestionTemplateModel);
        boolean isQuestionFormInvalid = checkQuestionFormForErrors(model, formQuestionTemplate);
        if (isQuestionFormInvalid) {
            return "add-question";
        }
        savingQuestionValueWhileAddingCategory(formQuestionTemplate, formQuestionTemplateModel);
        return goToAddPage(model, formQuestionTemplate);
    }

    private String goToAddPage(Model model, FormQuestionTemplate formQuestionTemplate) {
        saveCategoryToRepository(formQuestionTemplate);
        List<Category> categories = categoriesService.findAll();
        model.addAttribute("categories", categories);
        return "add-question";
    }

    private void saveCategoryToRepository(FormQuestionTemplate formQuestionTemplate) {
        Category subcategory = findSubcategory(formQuestionTemplate);
        Category category = Category.builder()
                .name(formQuestionTemplate.getCreatedCategory())
                .category(subcategory)
                .build();
        categoriesService.save(category);
    }

    private Category findSubcategory(FormQuestionTemplate formQuestionTemplate) {
        String superiorCategory = formQuestionTemplate.getSuperiorCategory();
        Optional<Category> subcategoryOptional = categoriesService.findById(superiorCategory);
        return subcategoryOptional.orElse(null);
    }

    private void savingQuestionValueWhileAddingCategory(FormQuestionTemplate formQuestionTemplate
            , FormQuestionTemplate formQuestionTemplateModel) {
        formQuestionTemplateModel.setQuestionValue(formQuestionTemplate.getQuestionValue());
    }

    private boolean checkQuestionFormForErrors(Model model, FormQuestionTemplate formQuestionTemplate) {
        boolean doesFormHaveErrors = checkForErrors(model, formQuestionTemplate);
        if (doesFormHaveErrors){
            List<Category> categories = categoriesService.findAll();
            model.addAttribute("categories", categories);
            return true;
        }
        return false;
    }

    private boolean checkForErrors(Model model, FormQuestionTemplate formQuestionTemplate) {
        if(!formQuestionTemplate.getCreatedCategory().matches("[a-zA-Z -,]{3,30}")) {
            model.addAttribute("errorMsg",
                    "Invalid category name, category name must have between 3 and 30 characters and only use -, special characters");
            return true;
        }
        if(categoriesService.findById(formQuestionTemplate.getCreatedCategory()).isPresent()) {
            model.addAttribute("errorMsg", "There already is category with this name");
            return true;
        }
        return false;
    }
}
