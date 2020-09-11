package com.maxbys.strona_z_poradami_projekt.questions;

import com.maxbys.strona_z_poradami_projekt.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionsService {

    private final QuestionsRepository questionsRepository;

    @Autowired
    public QuestionsService(QuestionsRepository questionsRepository) {
        this.questionsRepository = questionsRepository;
    }

    public List<Question> findAll(){
        return questionsRepository.findAll();
    }

    public Optional<Question> findById(Long id){
        return questionsRepository.findById(id);
    }

    public void save(Question question){
        questionsRepository.save(question);
    }

    public void deleteById(Long id){
        questionsRepository.deleteById(id);
    }

    public Page<Question> findAllByCategoryIs(String categoryId, Pageable pageable) {
        return questionsRepository.findAllByCategoryNameIs(categoryId, pageable);
    }

    public Optional<Question> findByIdAndCategoryName(Long id, String categoryId) {
        return questionsRepository.findByIdAndCategoryName(id, categoryId);
    }

    public Optional<Category> getCategoryFromQuestionById(Long id) {
        return questionsRepository.getCategoryFromQuestionById(id);
    }

    public List<Question> findTop5ByOrderByIdDesc() {
        return questionsRepository.findTop5ByOrderByIdDesc();
    }

    public Page<Question> findAllByUserEmailIs(String email, Pageable pageable) {
        return questionsRepository.findAllByUserEmailIs(email, pageable);
    }

}
