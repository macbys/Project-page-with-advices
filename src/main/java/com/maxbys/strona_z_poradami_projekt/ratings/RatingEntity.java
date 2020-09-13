package com.maxbys.strona_z_poradami_projekt.ratings;

import com.maxbys.strona_z_poradami_projekt.answers.AnswerEntity;
import com.maxbys.strona_z_poradami_projekt.users.UserEntity;
import lombok.*;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Getter
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
