package com.maxbys.strona_z_poradami_projekt.users;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO{

    private String email;
    private String name;

    public static UserDTO apply(UserEntity userEntity) {
        UserDTO userDTO = UserDTO.builder()
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .build();
        return userDTO;
    }
}
