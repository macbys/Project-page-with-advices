package com.maxbys.page_with_tips_project.users;

import lombok.*;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO{

    private Long id;
    private String name;
    private String avatar;
    private String role;

    public static UserDTO apply(UserEntity userEntity) {
        UserDTO userDTO = UserDTO.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .avatar(Base64.encodeBase64String(userEntity.getAvatar()))
                .role(userEntity.getRole())
                .build();
        return userDTO;
    }
}
