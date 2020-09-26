package com.maxbys.page_with_tips_project.users;

import lombok.*;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO{

    private String email;
    private String name;
    private String avatar;

    public static UserDTO apply(UserEntity userEntity) {
        UserDTO userDTO = UserDTO.builder()
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .avatar(Base64.encodeBase64String(userEntity.getAvatar()))
                .build();
        return userDTO;
    }
}
