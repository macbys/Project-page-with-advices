package com.maxbys.page_with_tips_project.forgotten_password;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ForgottenPasswordTokenRepository extends JpaRepository<ForgottenPasswordToken, UserEntityEmbedded> {
}
