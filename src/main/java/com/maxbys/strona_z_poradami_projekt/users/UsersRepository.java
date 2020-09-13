package com.maxbys.strona_z_poradami_projekt.users;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);
    void deleteByEmail(String email);
}
