package com.maxbys.strona_z_poradami_projekt.forgotten_password;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgottenPasswordToken {
    @Id
    private String email;
    private String randomPathEnding;
    private LocalDateTime expiryDate;
}
