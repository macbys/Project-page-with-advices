package com.maxbys.strona_z_poradami_projekt.questions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class FormQuestionTemplate {

    private Long id;
    private String category;
    private String createdCategory;
    private String questionValue;
    private String superiorCategory;
}
