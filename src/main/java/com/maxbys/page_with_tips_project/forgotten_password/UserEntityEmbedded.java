package com.maxbys.page_with_tips_project.forgotten_password;

import com.maxbys.page_with_tips_project.users.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserEntityEmbedded implements Serializable {
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne
    private UserEntity userEntity;
}
