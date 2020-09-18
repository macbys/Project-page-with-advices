package com.maxbys.page_with_tips_project.users;

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
