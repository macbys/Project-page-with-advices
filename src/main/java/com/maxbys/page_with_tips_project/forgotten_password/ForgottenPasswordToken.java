package com.maxbys.page_with_tips_project.forgotten_password;

import lombok.*;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ForgottenPasswordToken {
    @EmbeddedId
    private UserEntityEmbedded userEntityId;
    private String randomPathEnding;
    private LocalDateTime expiryDate;
}
