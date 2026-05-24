package com.adventurekm.backend.repository;

import com.adventurekm.backend.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByToken(String token);
    List<Invitation> findByInvitedById(Long userId);
}
