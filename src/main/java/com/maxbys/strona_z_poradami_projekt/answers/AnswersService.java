package com.maxbys.strona_z_poradami_projekt.answers;

import com.maxbys.strona_z_poradami_projekt.questions.QuestionDTO;
import com.maxbys.strona_z_poradami_projekt.questions.QuestionEntity;
import com.maxbys.strona_z_poradami_projekt.questions.QuestionsRepository;
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
public class AnswersService {

    private final AnswersRepository answersRepository;
    private final UsersRepository usersRepository;
    private final QuestionsRepository questionsRepository;

    @Autowired
    public AnswersService(AnswersRepository answersRepository, UsersRepository usersRepository, QuestionsRepository questionsRepository) {
        this.answersRepository = answersRepository;
        this.usersRepository = usersRepository;
        this.questionsRepository = questionsRepository;
    }

    public AnswerDTO findById(Long answerId) {
        Optional<AnswerEntity> answerEntityOptional = answersRepository.findById(answerId);
        AnswerEntity answerEntity = answerEntityOptional.orElseThrow(() ->
                new RuntimeException("Answer with id " + answerId + " doesn't exist"));
        AnswerDTO answerDTO = AnswerDTO.apply(answerEntity);
        return answerDTO;
    }

    public void save(AnswerDTO answerDTO) {
        UserEntity userEntity = getUserEntity(answerDTO);
        QuestionEntity questionEntity = getQuestionEntity(answerDTO);
        AnswerEntity answerEntity = AnswerEntity.apply(answerDTO, userEntity, questionEntity);
        answersRepository.save(answerEntity);
    }

    private QuestionEntity getQuestionEntity(AnswerDTO answerDTO) {
        QuestionDTO questionDTO = answerDTO.getQuestionDTO();
        Long questionId = questionDTO.getId();
        Optional<QuestionEntity> questionEntityOptional = questionsRepository.findById(questionId);
        return questionEntityOptional.orElseThrow(() ->
                new RuntimeException("Question with id " + questionId + " doesn't exist"));
    }

    private UserEntity getUserEntity(AnswerDTO answerDTO) {
        UserDTO userDTO = answerDTO.getUserDTO();
        String email = userDTO.getEmail();
        Optional<UserEntity> userEntityOptional = usersRepository.findByEmail(email);
        return userEntityOptional.orElseThrow(() ->
                new RuntimeException("User with email " + email + "doesn't exist"));
    }

    public void deleteById(Long id){
        answersRepository.deleteById(id);
    }

    public Page<AnswerDTO> findAllByQuestionId(Long id, Pageable pageable) {
        Page<AnswerEntity> allByQuestionId = answersRepository.findAllByQuestionEntityId(id, pageable);
        List<AnswerDTO> answerDTOList = allByQuestionId.stream()
                .map(ae -> AnswerDTO.apply(ae))
                .collect(Collectors.toList());
        return new PageImpl<AnswerDTO>(answerDTOList, pageable, allByQuestionId.getTotalElements());
    }

    public Page<AnswerDTO> findAllByUser_Email(String email, Pageable pageable) {
        Page<AnswerEntity> allByUser_email = answersRepository.findAllByUserEntityEmail(email, pageable);
        List<AnswerDTO> answerDTOList = allByUser_email.stream()
                .map(ae -> AnswerDTO.apply(ae))
                .collect(Collectors.toList());
        return new PageImpl<AnswerDTO>(answerDTOList, pageable, allByUser_email.getTotalElements());
    }
}
