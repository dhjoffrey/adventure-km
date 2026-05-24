package com.adventurekm.backend.controller;

import com.adventurekm.backend.dto.request.AdventureCreateRequest;
import com.adventurekm.backend.dto.request.AdventureUpdateRequest;
import com.adventurekm.backend.dto.response.AdventureResponse;
import com.adventurekm.backend.dto.response.AdventureSummaryResponse;
import com.adventurekm.backend.service.AdventureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/adventures")
@RequiredArgsConstructor
public class AdventureController {

    private final AdventureService adventureService;

    @GetMapping
    public List<AdventureSummaryResponse> listPublished() {
        return adventureService.listPublished();
    }

    @GetMapping("/{id}")
    public AdventureResponse getById(@PathVariable Long id) {
        return adventureService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdventureResponse create(@AuthenticationPrincipal UserDetails user,
                                    @Valid @RequestBody AdventureCreateRequest request) {
        return adventureService.create(user.getUsername(), request);
    }

    @PutMapping("/{id}")
    public AdventureResponse update(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails user,
                                    @Valid @RequestBody AdventureUpdateRequest request) {
        return adventureService.update(id, user.getUsername(), request);
    }

    @PostMapping("/{id}/publish")
    public AdventureResponse publish(@PathVariable Long id,
                                     @AuthenticationPrincipal UserDetails user) {
        return adventureService.publish(id, user.getUsername());
    }

    @PostMapping("/{id}/gpx")
    public AdventureResponse uploadGpx(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails user,
                                        @RequestParam("file") MultipartFile file) {
        return adventureService.processGpx(id, user.getUsername(), file);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        adventureService.delete(id, user.getUsername());
    }
}
