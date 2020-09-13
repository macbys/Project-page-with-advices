package com.maxbys.strona_z_poradami_projekt.forgotten_password;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgottenPasswordToken {
    @EmbeddedId
    private UserEntityEmbedded email;
    private String randomPathEnding;
    private LocalDateTime expiryDate;
}
