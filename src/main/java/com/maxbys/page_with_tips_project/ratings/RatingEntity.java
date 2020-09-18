package com.maxbys.page_with_tips_project.ratings;

import com.maxbys.page_with_tips_project.answers.AnswerEntity;
import com.maxbys.page_with_tips_project.users.UserEntity;
import lombok.*;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Getter
@EqualsAndHashCode
public class RatingEntity {

    @EmbeddedId
    private RatingId ratingId;
    @Setter
    private Byte value;

    public static RatingEntity apply(Byte value, UserEntity userEntity, AnswerEntity answerEntity) {
        RatingEntity ratingEntity = new RatingEntity();
        ratingEntity.ratingId = new RatingId(userEntity, answerEntity);
        ratingEntity.value = value;
        return ratingEntity;
    }
}
