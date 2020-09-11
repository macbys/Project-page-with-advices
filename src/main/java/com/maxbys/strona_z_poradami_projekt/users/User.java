package com.maxbys.strona_z_poradami_projekt.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.maxbys.strona_z_poradami_projekt.answers.Answer;
import com.maxbys.strona_z_poradami_projekt.comments.Comment;
import com.maxbys.strona_z_poradami_projekt.questions.Question;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    private String email;
    private String name;
    @JsonIgnore
    private String password;

//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private Set<Question> questions;
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    @OneToMany(mappedBy = "user")
//    private Set<Answer> answers;
//    @OneToMany(mappedBy = "user")
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private Set<Comment> comments;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("USER"));
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
}
