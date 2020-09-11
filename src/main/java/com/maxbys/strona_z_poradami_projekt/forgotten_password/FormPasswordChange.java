package com.maxbys.strona_z_poradami_projekt.forgotten_password;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FormPasswordChange {
    private String password;
    private String passwordRepeated;
}
