package com.maxbys.strona_z_poradami_projekt.questions;

import com.maxbys.strona_z_poradami_projekt.categories.CategoriesRepository;
import com.maxbys.strona_z_poradami_projekt.categories.CategoryEntity;
import com.maxbys.strona_z_poradami_projekt.users.UserDTO;
import com.maxbys.strona_z_poradami_projekt.users.UserEntity;
import com.maxbys.strona_z_poradami_projekt.users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionsService {

    private final QuestionsRepository questionsRepository;
    private final UsersRepository usersRepository;
    private final CategoriesRepository categoriesRepository;

    @Autowired
    public QuestionsService(QuestionsRepository questionsRepository, UsersRepository usersRepository, CategoriesRepository categoriesRepository) {
        this.questionsRepository = questionsRepository;
        this.usersRepository = usersRepository;
        this.categoriesRepository = categoriesRepository;
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

    public Page<QuestionDTO> findAllByCategoryIs(String categoryId, Pageable pageable) {
        Page<QuestionEntity> allByCategoryNameIs = questionsRepository.findAllByCategoryEntityNameIs(categoryId, pageable);
        List<QuestionDTO> questionDTOList = allByCategoryNameIs.stream()
                .map(qe -> QuestionDTO.apply(qe))
                .collect(Collectors.toList());
        PageImpl<QuestionDTO> questionDTOS = new PageImpl<>(questionDTOList, pageable, allByCategoryNameIs.getTotalPages());
        return questionDTOS;
    }

    public List<QuestionDTO> findTop5ByOrderByIdDesc() {
        List<QuestionEntity> top5ByOrderByIdDesc = questionsRepository.findTop5ByOrderByIdDesc();
        List<QuestionDTO> questionDTOList = top5ByOrderByIdDesc.stream()
                .map(qe -> QuestionDTO.apply(qe))
                .collect(Collectors.toList());
        return questionDTOList;
    }

    public Page<QuestionDTO> findAllByUserEmailIs(String email, Pageable pageable) {
        Page<QuestionEntity> allByCategoryNameIs = questionsRepository.findAllByUserEntityEmailIs(email, pageable);
        List<QuestionDTO> questionDTOList = allByCategoryNameIs.stream()
                .map(qe -> QuestionDTO.apply(qe))
                .collect(Collectors.toList());
        PageImpl<QuestionDTO> questionDTOS = new PageImpl<>(questionDTOList, pageable, allByCategoryNameIs.getTotalPages());
        return questionDTOS;
    }
}
