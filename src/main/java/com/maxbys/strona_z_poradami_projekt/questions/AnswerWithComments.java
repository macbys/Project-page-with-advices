package com.maxbys.strona_z_poradami_projekt.questions;

import com.maxbys.strona_z_poradami_projekt.answers.AnswerDTO;
import com.maxbys.strona_z_poradami_projekt.comments.CommentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerWithComments {
    private AnswerDTO answerDTO;
    private Page<CommentDTO> comments;
}
