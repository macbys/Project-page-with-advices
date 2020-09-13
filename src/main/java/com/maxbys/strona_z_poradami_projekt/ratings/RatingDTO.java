package com.maxbys.strona_z_poradami_projekt.ratings;

import com.maxbys.strona_z_poradami_projekt.answers.AnswerDTO;
import com.maxbys.strona_z_poradami_projekt.answers.AnswerEntity;
import com.maxbys.strona_z_poradami_projekt.users.UserDTO;
import com.maxbys.strona_z_poradami_projekt.users.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingDTO {

    private AnswerDTO answerDTO;
    private UserDTO userDTO;
    private Byte value;

    public static RatingDTO apply(RatingEntity ratingEntity) {
        RatingId ratingId = ratingEntity.getRatingId();
        AnswerEntity answerEntity = ratingId.getAnswerEntity();
        AnswerDTO answerDTO = AnswerDTO.apply(answerEntity);
        UserEntity userEntity = ratingId.getUserEntity();
        UserDTO userDTO = UserDTO.apply(userEntity);
        RatingDTO ratingDTO = RatingDTO.builder()
                .answerDTO(answerDTO)
                .userDTO(userDTO)
                .value(ratingEntity.getValue())
                .build();
        return ratingDTO;
    }
}
