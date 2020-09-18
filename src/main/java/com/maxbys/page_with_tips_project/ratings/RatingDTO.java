package com.maxbys.page_with_tips_project.ratings;

import com.maxbys.page_with_tips_project.answers.AnswerDTO;
import com.maxbys.page_with_tips_project.answers.AnswerEntity;
import com.maxbys.page_with_tips_project.users.UserDTO;
import com.maxbys.page_with_tips_project.users.UserEntity;
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
