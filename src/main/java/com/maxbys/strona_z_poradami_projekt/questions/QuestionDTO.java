package com.maxbys.strona_z_poradami_projekt.questions;

import com.maxbys.strona_z_poradami_projekt.categories.CategoryDTO;
import com.maxbys.strona_z_poradami_projekt.users.UserDTO;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {

    private Long id;
    private String value;
    private UserDTO userDTO;
    private CategoryDTO categoryDTO;
    private LocalDate creationDate;

    public static QuestionDTO apply(QuestionEntity questionEntity) {
        QuestionDTO questionDTO = QuestionDTO.builder()
                .id(questionEntity.getId())
                .value(questionEntity.getValue())
                .userDTO(UserDTO.apply(questionEntity.getUserEntity()))
                .categoryDTO(CategoryDTO.apply(questionEntity.getCategoryEntity()))
                .creationDate(questionEntity.getCreationDate())
                .build();
        return questionDTO;
    }
}