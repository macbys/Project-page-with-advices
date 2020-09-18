package com.maxbys.page_with_tips_project.ratings;

import com.maxbys.page_with_tips_project.answers.AnswerEntity;
import com.maxbys.page_with_tips_project.users.UserEntity;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Embeddable
public class RatingId implements Serializable {
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    private UserEntity userEntity;
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    private AnswerEntity answerEntity;
}
