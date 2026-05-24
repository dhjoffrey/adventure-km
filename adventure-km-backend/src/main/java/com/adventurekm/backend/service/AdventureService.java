package com.adventurekm.backend.service;

import com.adventurekm.backend.dto.request.AdventureCreateRequest;
import com.adventurekm.backend.dto.request.AdventureUpdateRequest;
import com.adventurekm.backend.dto.response.AdventureResponse;
import com.adventurekm.backend.dto.response.AdventureSummaryResponse;
import com.adventurekm.backend.exception.BadRequestException;
import com.adventurekm.backend.exception.ForbiddenException;
import com.adventurekm.backend.exception.ResourceNotFoundException;
import com.adventurekm.backend.mapper.AdventureMapper;
import com.adventurekm.backend.model.*;
import com.adventurekm.backend.repository.AdventureRepository;
import com.adventurekm.backend.repository.EquipmentItemRepository;
import com.adventurekm.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdventureService {

    private final AdventureRepository adventureRepository;
    private final UserRepository userRepository;
    private final EquipmentItemRepository equipmentItemRepository;
    private final AdventureMapper adventureMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<AdventureSummaryResponse> listPublished() {
        return adventureMapper.toSummaryResponseList(
                adventureRepository.findByStatusOrderByDateDesc(AdventureStatus.PUBLISHED));
    }

    @Transactional(readOnly = true)
    public List<AdventureSummaryResponse> listByUser(Long userId) {
        return adventureMapper.toSummaryResponseList(
                adventureRepository.findByUser_IdOrderByDateDesc(userId));
    }

    @Transactional(readOnly = true)
    public AdventureResponse getById(Long id) {
        Adventure adventure = adventureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adventure", id));
        return adventureMapper.toResponse(adventure);
    }

    @Transactional
    public AdventureResponse create(String username, AdventureCreateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));

        Adventure adventure = Adventure.builder()
                .user(user)
                .title(request.title())
                .date(request.date())
                .content(request.content())
                .type(request.type() != null ? AdventureType.valueOf(request.type()) : null)
                .difficulty(request.difficulty())
                .build();

        if (request.equipmentIds() != null && !request.equipmentIds().isEmpty()) {
            adventure.setEquipment(new HashSet<>(equipmentItemRepository.findAllById(request.equipmentIds())));
        }

        adventure = adventureRepository.save(adventure);
        return adventureMapper.toResponse(adventure);
    }

    @Transactional
    public AdventureResponse update(Long id, String username, AdventureUpdateRequest request) {
        Adventure adventure = adventureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adventure", id));
        if (!adventure.getUser().getUsername().equals(username)) {
            throw new ForbiddenException("Not authorized to edit this adventure");
        }

        if (request.title() != null) adventure.setTitle(request.title());
        if (request.date() != null) adventure.setDate(request.date());
        if (request.content() != null) adventure.setContent(request.content());
        if (request.type() != null) adventure.setType(AdventureType.valueOf(request.type()));
        if (request.difficulty() != null) adventure.setDifficulty(request.difficulty());
        if (request.equipmentIds() != null) {
            adventure.setEquipment(new HashSet<>(equipmentItemRepository.findAllById(request.equipmentIds())));
        }
        adventure.setUpdatedAt(LocalDateTime.now());

        adventure = adventureRepository.save(adventure);
        return adventureMapper.toResponse(adventure);
    }

    @Transactional
    public AdventureResponse publish(Long id, String username) {
        Adventure adventure = adventureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adventure", id));
        if (!adventure.getUser().getUsername().equals(username)) {
            throw new BadRequestException("Not authorized to publish this adventure");
        }
        adventure.setStatus(AdventureStatus.PUBLISHED);
        adventure.setUpdatedAt(LocalDateTime.now());
        adventure = adventureRepository.save(adventure);

        eventPublisher.publishEvent(new AdventurePublishedEvent(adventure));

        return adventureMapper.toResponse(adventure);
    }

    @Transactional
    public void delete(Long id, String username) {
        Adventure adventure = adventureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adventure", id));
        if (!adventure.getUser().getUsername().equals(username)) {
            throw new BadRequestException("Not authorized to delete this adventure");
        }
        adventureRepository.delete(adventure);
    }
}
