package com.adventurekm.backend.controller;

import com.adventurekm.backend.dto.request.EquipmentCreateRequest;
import com.adventurekm.backend.dto.request.InvitationCreateRequest;
import com.adventurekm.backend.dto.response.EquipmentItemResponse;
import com.adventurekm.backend.dto.response.InvitationResponse;
import com.adventurekm.backend.dto.response.UserResponse;
import com.adventurekm.backend.exception.ResourceNotFoundException;
import com.adventurekm.backend.mapper.AdventureMapper;
import com.adventurekm.backend.mapper.UserMapper;
import com.adventurekm.backend.model.EquipmentCategory;
import com.adventurekm.backend.model.EquipmentItem;
import com.adventurekm.backend.model.Invitation;
import com.adventurekm.backend.model.User;
import com.adventurekm.backend.repository.EquipmentItemRepository;
import com.adventurekm.backend.repository.UserRepository;
import com.adventurekm.backend.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final EquipmentItemRepository equipmentItemRepository;
    private final InvitationService invitationService;
    private final UserMapper userMapper;
    private final AdventureMapper adventureMapper;

    @GetMapping("/users")
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream().map(userMapper::toResponse).toList();
    }

    @PostMapping("/invitations")
    @ResponseStatus(HttpStatus.CREATED)
    public InvitationResponse createInvitation(@AuthenticationPrincipal UserDetails userDetails,
                                                @RequestBody InvitationCreateRequest request) {
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", userDetails.getUsername()));
        Invitation invitation = invitationService.createInvitation(admin, request.email());
        return toInvitationResponse(invitation);
    }

    @GetMapping("/invitations")
    public List<InvitationResponse> listInvitations(@AuthenticationPrincipal UserDetails userDetails) {
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", userDetails.getUsername()));
        return invitationService.findByInviter(admin.getId()).stream()
                .map(this::toInvitationResponse).toList();
    }

    private InvitationResponse toInvitationResponse(Invitation inv) {
        return new InvitationResponse(inv.getId(), inv.getToken(), inv.getEmail(),
                inv.getExpiresAt(), inv.getUsedAt());
    }

    @GetMapping("/equipment")
    public List<EquipmentItemResponse> listEquipment() {
        return equipmentItemRepository.findAll().stream()
                .map(adventureMapper::toEquipmentResponse).toList();
    }

    @PostMapping("/equipment")
    @ResponseStatus(HttpStatus.CREATED)
    public EquipmentItemResponse createEquipment(@RequestBody EquipmentCreateRequest request) {
        EquipmentItem item = EquipmentItem.builder()
                .name(request.name())
                .category(EquipmentCategory.valueOf(request.category()))
                .iconKey(request.iconKey())
                .pixelSpriteKey(request.pixelSpriteKey())
                .build();
        return adventureMapper.toEquipmentResponse(equipmentItemRepository.save(item));
    }

    @DeleteMapping("/equipment/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEquipment(@PathVariable Long id) {
        if (!equipmentItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("EquipmentItem", id);
        }
        equipmentItemRepository.deleteById(id);
    }
}
