package com.maxbys.page_with_tips_project.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.maxbys.page_with_tips_project.forgotten_password.FormPasswordChange;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity implements UserDetails, Serializable {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
    private String email;
    @Getter
    private String name;
    @JsonIgnore
    private String password;
    @Setter
    @Getter
    @Lob
    private byte[] avatar;
    @Getter
    private String role;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static UserEntity apply(FormUserTemplateDTO formUserTemplateDTO) {
        UserEntity userEntity = new UserEntity();
        userEntity.id = formUserTemplateDTO.getId();
        userEntity.email = formUserTemplateDTO.getEmail();
        userEntity.name = formUserTemplateDTO.getName();
        userEntity.password = formUserTemplateDTO.getPassword();
        try{
            userEntity.avatar = formUserTemplateDTO.getAvatar() == null? null : formUserTemplateDTO.getAvatar().getBytes();
        }catch (IOException ex){}
        userEntity.role = formUserTemplateDTO.getRole();
        return userEntity;
    }

    public static UserEntity updateWithoutPassword(UserEntity userEntity, FormUserTemplateDTO formUserTemplateDTO) {
        userEntity.name = formUserTemplateDTO.getName();
        userEntity.email = formUserTemplateDTO.getEmail();
        try{
            userEntity.avatar = formUserTemplateDTO.getAvatar() == null? null : formUserTemplateDTO.getAvatar().getBytes();
        }catch (IOException ex){}
        return userEntity;
    }

    public static UserEntity updateWithoutPasswordAndAvatar(UserEntity userEntity, FormUserTemplateDTO formUserTemplateDTO) {
        userEntity.name = formUserTemplateDTO.getName();
        userEntity.email = formUserTemplateDTO.getEmail();
        return userEntity;
    }

    public static UserEntity changeUserPassword(UserEntity userEntity, FormPasswordChange formPasswordChange) {
        userEntity.password = formPasswordChange.getPassword();
        return userEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, name);
    }
}
