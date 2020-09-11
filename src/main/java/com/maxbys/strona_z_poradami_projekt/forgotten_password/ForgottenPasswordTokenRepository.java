package com.maxbys.strona_z_poradami_projekt.forgotten_password;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ForgottenPasswordTokenRepository extends JpaRepository<ForgottenPasswordToken, String> {
}
