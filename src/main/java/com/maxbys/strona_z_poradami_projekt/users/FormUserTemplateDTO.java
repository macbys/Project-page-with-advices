package com.maxbys.strona_z_poradami_projekt.users;

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
