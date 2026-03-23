package com.aziz.demosec.repository;

import com.aziz.demosec.domain.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // ✅ Trouver un token par sa valeur
    Optional<PasswordResetToken> findByToken(String token);

    // ✅ Supprimer l'ancien token d'un user avant d'en créer un nouveau
    void deleteByUser_Id(Long userId);
}