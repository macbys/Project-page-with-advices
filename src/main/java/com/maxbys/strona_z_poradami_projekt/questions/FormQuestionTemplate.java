package com.maxbys.strona_z_poradami_projekt.questions;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FormQuestionTemplate {
    private String category;
    private String createdCategory;
    private String questionValue;
    private String superiorCategory;
}
