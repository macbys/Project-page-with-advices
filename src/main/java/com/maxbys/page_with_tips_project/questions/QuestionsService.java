package com.maxbys.page_with_tips_project.questions;

import com.maxbys.page_with_tips_project.categories.CategoriesRepository;
import com.maxbys.page_with_tips_project.categories.CategoryEntity;
import com.maxbys.page_with_tips_project.questions.question_view.QuestionView;
import com.maxbys.page_with_tips_project.questions.question_view.QuestionViewService;
import com.maxbys.page_with_tips_project.users.UserDTO;
import com.maxbys.page_with_tips_project.users.UserEntity;
import com.maxbys.page_with_tips_project.users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionsService {

    private final QuestionsRepository questionsRepository;
    private final UsersRepository usersRepository;
    private final CategoriesRepository categoriesRepository;
    private final QuestionViewService questionViewService;

    @Autowired
    public QuestionsService(QuestionsRepository questionsRepository, UsersRepository usersRepository, CategoriesRepository categoriesRepository, QuestionViewService questionViewService) {
        this.questionsRepository = questionsRepository;
        this.usersRepository = usersRepository;
        this.categoriesRepository = categoriesRepository;
        this.questionViewService = questionViewService;
    }

    public QuestionDTO findById(Long questionId){
        Optional<QuestionEntity> questionEntityOptional = questionsRepository.findById(questionId);
        QuestionEntity questionEntity = questionEntityOptional.orElseThrow(() ->
                new RuntimeException("Question with id " + questionId + " doesn't exist"));
        QuestionDTO questionDTO = QuestionDTO.apply(questionEntity);
        return questionDTO;
    }

    public void save(FormQuestionTemplate formQuestionTemplate, UserDTO userDTO) {
        String categoryName = formQuestionTemplate.getCategory();
        CategoryEntity categoryEntity = categoriesRepository.findById(categoryName).orElseThrow(() ->
                new RuntimeException("Category with name " + categoryName + " doesn't exist"));
        String email = userDTO.getEmail();
        UserEntity userEntity = usersRepository.findByEmail(email).orElseThrow(() ->
                new RuntimeException("User with email " + email + " doesn't exist"));
        QuestionEntity questionEntity = QuestionEntity.apply(formQuestionTemplate, userEntity, categoryEntity);
        questionsRepository.save(questionEntity);
    }

    public void update(FormQuestionTemplate formQuestionTemplate, Long questionId) {
        Optional<QuestionEntity> questionEntityOptional = questionsRepository.findById(questionId);
        QuestionEntity questionEntity = questionEntityOptional.orElseThrow(() ->
                new RuntimeException("Question with id " + questionId + " doesn't exist"));
        questionEntity = QuestionEntity.update(formQuestionTemplate, questionEntity);
        questionsRepository.save(questionEntity);
    }

    public void deleteById(Long id){
        questionsRepository.deleteById(id);
    }

    @Transactional
    public Page<QuestionDTO> findAllByCategoryIs(String categoryId, Pageable pageable) {
        Page<QuestionEntity> allByCategoryNameIs = questionsRepository.findAllByCategoryEntityNameIs(categoryId, pageable);
        List<QuestionDTO> questionDTOList = allByCategoryNameIs.stream()
                .map(qe -> QuestionDTO.apply(qe))
                .collect(Collectors.toList());
        PageImpl<QuestionDTO> questionDTOS = new PageImpl<>(questionDTOList, pageable, allByCategoryNameIs.getTotalPages());
        return questionDTOS;
    }

    @Transactional
    public List<QuestionDTO> findTop5ByOrderByIdDesc() {
        List<QuestionEntity> top5ByOrderByIdDesc = questionsRepository.findTop5ByOrderByIdDesc();
        List<QuestionDTO> questionDTOList = top5ByOrderByIdDesc.stream()
                .map(qe -> QuestionDTO.apply(qe))
                .collect(Collectors.toList());
        return questionDTOList;
    }

    @Transactional
    public Page<QuestionDTO> findAllByUserEmailIs(String email, Pageable pageable) {
        Page<QuestionEntity> allByCategoryNameIs = questionsRepository.findAllByUserEntityEmailIs(email, pageable);
        List<QuestionDTO> questionDTOList = allByCategoryNameIs.stream()
                .map(qe -> QuestionDTO.apply(qe))
                .collect(Collectors.toList());
        PageImpl<QuestionDTO> questionDTOS = new PageImpl<>(questionDTOList, pageable, allByCategoryNameIs.getTotalPages());
        return questionDTOS;
    }

    public Page<QuestionDTO> findQuestionsContainingString(String searchedString, Pageable pageable) {
        Page<QuestionEntity> questionEntityPage = questionsRepository.findQuestionsContainingString(searchedString, pageable);
        List<QuestionDTO> questionDTOList = questionEntityPage.stream()
                .map(QuestionDTO::apply)
                .collect(Collectors.toList());
        return new PageImpl<>(questionDTOList, pageable, questionEntityPage.getTotalElements());
    }

    @Transactional
    public void addQuestionView(String email, Long questionId) {
        Optional<UserEntity> userEntityOptional = usersRepository.findByEmail(email);
        UserEntity userEntity = null;
        if(userEntityOptional.isPresent()) {
            userEntity = userEntityOptional.get();
        }
        Optional<QuestionEntity> questionEntityOptional = questionsRepository.findById(questionId);
        QuestionEntity questionEntity = questionEntityOptional.orElseThrow(() ->
                new RuntimeException("Question with id " + questionId + " doesn't exist"));
        questionEntity.incrementAllViews();
        questionsRepository.save(questionEntity);
        QuestionView questionView = QuestionView.builder()
                .userEntity(userEntity)
                .question(questionEntity)
                .creationTime(new Date())
                .build();
        questionViewService.save(questionView);
    }

    public Page<QuestionDTO> getMostPopularQuestionsToday() {
        return questionViewService.getMostPopularQuestionsToday();
    }

    public Page<QuestionDTO> getMostPopularQuestionsInSevenDays() {
        return questionViewService.getMostPopularQuestionsInSevenDays();
    }

    public Page<QuestionDTO> getMostPopularQuestionsInThirtyDays() {
        return questionViewService.getMostPopularQuestionsInThirtyDays();
    }

    public List<QuestionDTO> findTop5MostPopularQuestionsSincePageExists() {
        List<QuestionEntity> questionEntities = questionsRepository.findTop5ByOrderByAllViewsOfThisQuestionDesc();
        List<QuestionDTO> questionDTOS = questionEntities.stream()
                .map(QuestionDTO::apply)
                .collect(Collectors.toList());
        return questionDTOS;
    }
}
