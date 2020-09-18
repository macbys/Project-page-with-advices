package com.maxbys.page_with_tips_project.answers;

import com.maxbys.page_with_tips_project.questions.QuestionEntity;
import com.maxbys.page_with_tips_project.users.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
public class AnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    private UserEntity userEntity;
    @Setter
    private Integer rating;
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    private QuestionEntity questionEntity;
    @Column(length = 1800)
    private String value;
    @CreationTimestamp
    private LocalDate creationDate;

    public static AnswerEntity apply(AnswerDTO answerDTO, UserEntity userEntity, QuestionEntity questionEntity) {
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.id = answerDTO.getId();
        answerEntity.userEntity = userEntity;
        answerEntity.rating = answerDTO.getRating();
        answerEntity.questionEntity = questionEntity;
        answerEntity.value = answerDTO.getValue();
        answerEntity.creationDate = answerDTO.getCreationDate();
        return answerEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerEntity that = (AnswerEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
