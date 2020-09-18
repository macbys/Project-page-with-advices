package com.maxbys.page_with_tips_project.questions;

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
