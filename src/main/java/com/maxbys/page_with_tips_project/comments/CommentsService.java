package com.maxbys.page_with_tips_project.comments;

import com.maxbys.page_with_tips_project.answers.AnswerEntity;
import com.maxbys.page_with_tips_project.answers.AnswersRepository;
import com.maxbys.page_with_tips_project.users.UserEntity;
import com.maxbys.page_with_tips_project.users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private final UsersRepository usersRepository;
    private final AnswersRepository answersRepository;

    @Autowired
    public CommentsService(CommentsRepository commentsRepository, UsersRepository usersRepository, AnswersRepository answersRepository) {
        this.commentsRepository = commentsRepository;
        this.usersRepository = usersRepository;
        this.answersRepository = answersRepository;
    }

    public CommentDTO findById(Long id){
        Optional<CommentEntity> commentEntityOptional = commentsRepository.findById(id);
        CommentEntity commentEntity = commentEntityOptional.orElseThrow(() ->
                new RuntimeException("Comment with id " + id + " doesn't exist"));
        return CommentDTO.apply(commentEntity);
    }

    public void save(CommentDTO commentDTO, Long answerId, String email){
        Optional<AnswerEntity> answerEntityOptional = answersRepository.findById(answerId);
        AnswerEntity answerEntity = answerEntityOptional.orElseThrow(() ->
                new RuntimeException("Answer with id " + answerId + " doesn't exist"));
        Optional<UserEntity> userEntityOptional = usersRepository.findByEmail(email);
        UserEntity userEntity = userEntityOptional.orElseThrow(() ->
                new RuntimeException("User with email " + email + " deosn't exist"));
        CommentEntity commentEntity = CommentEntity.apply(commentDTO, answerEntity, userEntity);
        commentsRepository.save(commentEntity);
    }

    public void deleteById(Long id){
        commentsRepository.deleteById(id);
    }

    public Page<CommentDTO> findAllByAnswerId(Long id, Pageable pageable) {
        Page<CommentEntity> commentEntityPage = commentsRepository.findAllByAnswerEntityId(id, pageable);
        List<CommentDTO> commentDTOList = commentEntityPage.stream()
                .map(ce -> CommentDTO.apply(ce))
                .collect(Collectors.toList());
        return new PageImpl<>(commentDTOList, pageable, commentEntityPage.getTotalElements());
    }

    public Page<CommentDTO> findAllByUser_Email(String email, Pageable pageable) {
        Page<CommentEntity> commentEntityPage = commentsRepository.findAllByUserEntity_Email(email, pageable);
        List<CommentDTO> commentDTOList = commentEntityPage.stream()
                .map(ce -> CommentDTO.apply(ce))
                .collect(Collectors.toList());
        return new PageImpl<>(commentDTOList, pageable, commentEntityPage.getTotalElements());
    }
}
