package com.maxbys.page_with_tips_project.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class FormUserTemplateDTO {

    private Long id;
    private String name;
    private String email;
    private String password;
    private String password_repeated;
}
