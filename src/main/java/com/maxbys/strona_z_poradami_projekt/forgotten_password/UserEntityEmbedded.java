package com.maxbys.strona_z_poradami_projekt.forgotten_password;

import com.maxbys.strona_z_poradami_projekt.users.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.persistence.Embeddable;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserEntityEmbedded implements Serializable {
    @OneToOne
    private UserEntity userEntity;
}
