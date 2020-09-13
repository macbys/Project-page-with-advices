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

@Controller
public class CategoriesController {

    private final CategoriesService categoriesService;

    @Autowired
    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping("/categories")
    public String showAllSuperiorCategories(Model model, Pageable pageable) {
        Page<CategoryDTO> categoriesWithoutUpperCategory = categoriesService.findAllByCategoryIsNull(pageable);
        model.addAttribute("categories", categoriesWithoutUpperCategory);
        List<Integer> paginationNumbers = PaginationGenerator.createPaginationList(pageable.getPageNumber(), categoriesWithoutUpperCategory.getTotalPages());
        model.addAttribute("paginationNumbers", paginationNumbers);
        return "categories";
    }

    @GetMapping("/category/{id}")
    public String showSubcategoriesOfCategory(@PathVariable String id, Model model, Pageable pageable) {
        Page<CategoryDTO> subCategories = categoriesService.findAllByCategoryNameIs(id, pageable);
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
        return goToAddQuestionPage(model, formQuestionTemplate);
    }

    private String goToAddQuestionPage(Model model, FormQuestionTemplate formQuestionTemplate) {
        saveCategoryToRepository(formQuestionTemplate);
        List<CategoryDTO> categories = categoriesService.findAllByOrOrderByName();
        model.addAttribute("categories", categories);
        return "add-question";
    }

    private void saveCategoryToRepository(FormQuestionTemplate formQuestionTemplate) {
        CategoryDTO subcategory = findSubcategory(formQuestionTemplate);
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .name(formQuestionTemplate.getCreatedCategory())
                .superiorCategory(subcategory)
                .build();
        categoriesService.save(categoryDTO);
    }

    private CategoryDTO findSubcategory(FormQuestionTemplate formQuestionTemplate) {
        String superiorCategory = formQuestionTemplate.getSuperiorCategory();
        if(superiorCategory == "") {
            return null;
        }
        CategoryDTO subcategoryOptional = categoriesService.findById(superiorCategory);
        return subcategoryOptional;
    }

    private void savingQuestionValueWhileAddingCategory(FormQuestionTemplate formQuestionTemplate
            , FormQuestionTemplate formQuestionTemplateModel) {
        formQuestionTemplateModel.setQuestionValue(formQuestionTemplate.getQuestionValue());
    }

    private boolean checkQuestionFormForErrors(Model model, FormQuestionTemplate formQuestionTemplate) {
        boolean doesFormHaveErrors = checkForErrors(model, formQuestionTemplate);
        if (doesFormHaveErrors) {
            List<CategoryDTO> categories = categoriesService.findAllByOrOrderByName();
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
        CategoryDTO categoryDTO = null;
        try {
            categoryDTO = categoriesService.findById(formQuestionTemplate.getCreatedCategory());
        } catch (RuntimeException ex) {
        }
        if(categoryDTO != null) {
            model.addAttribute("errorMsg", "There already is category with this name");
            return true;
        }
        return false;
    }
}
