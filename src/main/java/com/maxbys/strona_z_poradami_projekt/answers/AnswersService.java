package com.maxbys.strona_z_poradami_projekt.answers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AnswersService {

    private final AnswersRepository answersRepository;

    @Autowired
    public AnswersService(AnswersRepository answersRepository) {
        this.answersRepository = answersRepository;
    }

    public List<Answer> findAll(){
        return answersRepository.findAll();
    }

    public Optional<Answer> findById(Long id){
        return answersRepository.findById(id);
    }

    public void save(Answer answer){
        answersRepository.save(answer);
    }

    public void deleteById(Long id){
        answersRepository.deleteById(id);
    }

    public Optional<Answer> findByIdAndQuestionId(Long id, Long questionId) {
        return answersRepository.findByIdAndQuestionId(id, questionId);
    }

    public Page<Answer> findAllByQuestionId(Long id, Pageable pageable) {
        return answersRepository.findAllByQuestionId(id, pageable);
    }

    public Page<Answer> findAllByUser_Email(String email, Pageable pageable) {
        return answersRepository.findAllByUser_Email(email, pageable);
    }
}
