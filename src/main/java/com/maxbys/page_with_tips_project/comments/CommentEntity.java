package com.maxbys.page_with_tips_project.comments;

import com.maxbys.page_with_tips_project.answers.AnswerEntity;
import com.maxbys.page_with_tips_project.users.UserEntity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Entity
@EqualsAndHashCode
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    private AnswerEntity answerEntity;
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    private UserEntity userEntity;
    private String value;
    @CreationTimestamp
    private LocalDate creationDate;

    public static CommentEntity apply(CommentDTO commentDTO, AnswerEntity answerEntity, UserEntity userEntity) {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.id = commentDTO.getId();
        commentEntity.answerEntity = answerEntity;
        commentEntity.userEntity = userEntity;
        commentEntity.value = commentDTO.getValue();
        return commentEntity;
    }
}
