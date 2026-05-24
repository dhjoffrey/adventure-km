package com.adventurekm.backend.service;

import com.adventurekm.backend.exception.BadRequestException;
import com.adventurekm.backend.model.Invitation;
import com.adventurekm.backend.model.User;
import com.adventurekm.backend.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;

    public Invitation createInvitation(User invitedBy, String email) {
        Invitation invitation = Invitation.builder()
                .token(UUID.randomUUID().toString().replace("-", ""))
                .email(email)
                .invitedBy(invitedBy)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        return invitationRepository.save(invitation);
    }

    public Invitation validateAndConsume(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid invitation token"));
        if (invitation.getUsedAt() != null) {
            throw new BadRequestException("Invitation already used");
        }
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Invitation expired");
        }
        invitation.setUsedAt(LocalDateTime.now());
        return invitationRepository.save(invitation);
    }

    public List<Invitation> findByInviter(Long userId) {
        return invitationRepository.findByInvitedBy_Id(userId);
    }
}
