package com.maxbys.strona_z_poradami_projekt.comments;

import com.maxbys.strona_z_poradami_projekt.answers.AnswerDTO;
import com.maxbys.strona_z_poradami_projekt.users.UserDTO;
import lombok.*;
import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private Long id;
    private AnswerDTO answerDTO;
    private UserDTO userDTO;
    private String value;
    private LocalDate creationDate;

    public static CommentDTO apply(CommentEntity commentEntity) {
        CommentDTO commentDTO = CommentDTO.builder()
                .id(commentEntity.getId())
                .answerDTO(AnswerDTO.apply(commentEntity.getAnswerEntity()))
                .userDTO(UserDTO.apply(commentEntity.getUserEntity()))
                .value(commentEntity.getValue())
                .creationDate(commentEntity.getCreationDate())
                .build();
        return commentDTO;
    }
}
