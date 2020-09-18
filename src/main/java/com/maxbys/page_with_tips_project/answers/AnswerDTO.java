package com.maxbys.page_with_tips_project.answers;

import com.maxbys.page_with_tips_project.questions.QuestionDTO;
import com.maxbys.page_with_tips_project.users.UserDTO;
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
