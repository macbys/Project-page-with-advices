package com.maxbys.page_with_tips_project.forgotten_password;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class FormPasswordChange {
    private String password;
    private String passwordRepeated;
}
