package com.maxbys.strona_z_poradami_projekt.answers;

import com.maxbys.strona_z_poradami_projekt.questions.QuestionDTO;
import com.maxbys.strona_z_poradami_projekt.users.UserDTO;
import lombok.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerDTO {

    private Long id;
    private UserDTO userDTO;
    private Integer rating;
    private QuestionDTO questionDTO;
    private String value;
    private LocalDate creationDate;

    public static AnswerDTO apply(AnswerEntity answerEntity) {
        AnswerDTO answerDTO = AnswerDTO.builder()
                .id(answerEntity.getId())
                .userDTO(UserDTO.apply(answerEntity.getUserEntity()))
                .rating(answerEntity.getRating())
                .questionDTO(QuestionDTO.apply(answerEntity.getQuestionEntity()))
                .value(answerEntity.getValue())
                .creationDate(answerEntity.getCreationDate())
                .build();
        return answerDTO;
    }
}
