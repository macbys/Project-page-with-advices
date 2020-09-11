package com.maxbys.strona_z_poradami_projekt.forgotten_password;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ForgottenPasswordTokenService {

    private ForgottenPasswordTokenRepository forgottenPasswordTokenRepository;

    @Autowired
    public ForgottenPasswordTokenService(ForgottenPasswordTokenRepository forgottenPasswordTokenRepository) {
        this.forgottenPasswordTokenRepository = forgottenPasswordTokenRepository;
    }

    public void save(ForgottenPasswordToken forgottenPasswordToken) {
        forgottenPasswordTokenRepository.save(forgottenPasswordToken);
    }

    public Optional<ForgottenPasswordToken> findById(String id) {
        return forgottenPasswordTokenRepository.findById(id);
    }
}
