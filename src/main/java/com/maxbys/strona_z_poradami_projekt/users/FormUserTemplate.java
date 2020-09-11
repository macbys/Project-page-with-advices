package com.maxbys.strona_z_poradami_projekt.users;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FormUserTemplate {
    private String name;
    private String email;
    private String password;
    private String password_repeated;
}
