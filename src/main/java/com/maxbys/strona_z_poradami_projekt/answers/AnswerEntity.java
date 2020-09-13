package com.maxbys.strona_z_poradami_projekt.answers;

import com.maxbys.strona_z_poradami_projekt.questions.QuestionEntity;
import com.maxbys.strona_z_poradami_projekt.users.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
public class AnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
}
