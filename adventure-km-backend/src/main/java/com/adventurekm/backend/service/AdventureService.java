package com.adventurekm.backend.service;

import com.adventurekm.backend.dto.request.AdventureCreateRequest;
import com.adventurekm.backend.dto.request.AdventureUpdateRequest;
import com.adventurekm.backend.dto.response.AdventureResponse;
import com.adventurekm.backend.dto.response.AdventureSummaryResponse;
import com.adventurekm.backend.dto.response.GpxDataResponse;
import com.adventurekm.backend.exception.BadRequestException;
import com.adventurekm.backend.exception.ForbiddenException;
import com.adventurekm.backend.exception.ResourceNotFoundException;
import com.adventurekm.backend.mapper.AdventureMapper;
import com.adventurekm.backend.model.*;
import com.adventurekm.backend.model.Photo;
import com.adventurekm.backend.repository.AdventureRepository;
import com.adventurekm.backend.repository.EquipmentItemRepository;
import com.adventurekm.backend.repository.PhotoRepository;
import com.adventurekm.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private final GpxProcessingService gpxProcessingService;
    private final FileStorageService fileStorageService;
    private final PhotoRepository photoRepository;

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
    public List<AdventureSummaryResponse> listPublishedByUser(Long userId) {
        return adventureMapper.toSummaryResponseList(
                adventureRepository.findByUser_IdAndStatusOrderByDateDesc(userId, AdventureStatus.PUBLISHED));
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

        if (request.distanceKm() != null || request.elevationGainM() != null || request.durationMinutes() != null) {
            AdventureStats stats = new AdventureStats();
            stats.setAdventure(adventure);
            stats.setDistanceKm(request.distanceKm());
            stats.setElevationGainM(request.elevationGainM());
            stats.setDurationMinutes(request.durationMinutes());
            adventure.setStats(stats);
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
        if (request.distanceKm() != null || request.elevationGainM() != null || request.durationMinutes() != null) {
            AdventureStats stats = adventure.getStats();
            if (stats == null) {
                stats = new AdventureStats();
                stats.setAdventure(adventure);
                adventure.setStats(stats);
            }
            if (request.distanceKm() != null) stats.setDistanceKm(request.distanceKm());
            if (request.elevationGainM() != null) stats.setElevationGainM(request.elevationGainM());
            if (request.durationMinutes() != null) stats.setDurationMinutes(request.durationMinutes());
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
            throw new ForbiddenException("Not authorized to publish this adventure");
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
            throw new ForbiddenException("Not authorized to delete this adventure");
        }
        adventure.getEquipment().clear();
        adventureRepository.delete(adventure);
    }

    @Transactional
    public AdventureResponse processGpx(Long id, String username, MultipartFile file, boolean overwriteStats) {
        Adventure adventure = adventureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adventure", id));
        if (!adventure.getUser().getUsername().equals(username)) {
            throw new ForbiddenException("Not authorized to upload GPX for this adventure");
        }

        byte[] gpxBytes;
        try {
            gpxBytes = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read uploaded file", e);
        }

        if (overwriteStats) {
            GpxDataResponse gpxData = gpxProcessingService.process(new ByteArrayInputStream(gpxBytes));
            AdventureStats stats = adventure.getStats();
            if (stats == null) {
                stats = new AdventureStats();
                stats.setAdventure(adventure);
                adventure.setStats(stats);
            }
            stats.setDistanceKm(gpxData.distanceKm());
            stats.setElevationGainM(gpxData.elevationGainM());
            stats.setElevationLossM(gpxData.elevationLossM());
            stats.setDurationMinutes(gpxData.durationMinutes());
            stats.setMaxAltitudeM(gpxData.maxAltitudeM());
            stats.setMinAltitudeM(gpxData.minAltitudeM());
            stats.setAvgAltitudeM(gpxData.avgAltitudeM());
        }

        String gpxPath = fileStorageService.saveGpx(id, file);
        adventure.setGpxPath(gpxPath);
        adventure.setUpdatedAt(LocalDateTime.now());

        adventure = adventureRepository.save(adventure);
        return adventureMapper.toResponse(adventure);
    }

    @Transactional
    public AdventureResponse addPhoto(Long id, String username, MultipartFile file, String caption) {
        Adventure adventure = adventureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adventure", id));
        if (!adventure.getUser().getUsername().equals(username)) {
            throw new ForbiddenException("Not authorized to add photos to this adventure");
        }
        int currentCount = photoRepository.countByAdventureId(id);
        if (currentCount >= 5) {
            throw new BadRequestException("Maximum 5 photos per adventure");
        }

        int sortOrder = currentCount + 1;
        String filePath = fileStorageService.savePhoto(id, file, sortOrder);

        Photo photo = Photo.builder()
                .adventure(adventure)
                .filePath(filePath)
                .caption(caption.isEmpty() ? null : caption)
                .sortOrder(sortOrder)
                .build();
        photoRepository.save(photo);

        return adventureMapper.toResponse(adventureRepository.findById(id).orElseThrow());
    }

    @Transactional(readOnly = true)
    public GpxDataResponse getGpxData(Long id) {
        Adventure adventure = adventureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adventure", id));
        if (adventure.getGpxPath() == null) {
            throw new ResourceNotFoundException("GPX", id);
        }
        Path gpxFile = fileStorageService.resolve(adventure.getGpxPath());
        try (InputStream is = Files.newInputStream(gpxFile)) {
            return gpxProcessingService.process(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read GPX file", e);
        }
    }
}
