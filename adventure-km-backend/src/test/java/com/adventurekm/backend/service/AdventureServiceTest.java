package com.adventurekm.backend.service;

import com.adventurekm.backend.dto.request.AdventureCreateRequest;
import com.adventurekm.backend.dto.response.AdventureResponse;
import com.adventurekm.backend.dto.response.AdventureSummaryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class AdventureServiceTest {

    @Autowired
    private AdventureService adventureService;

    @Test
    void listPublishedAdventures() {
        List<AdventureSummaryResponse> adventures = adventureService.listPublished();
        assertThat(adventures).hasSize(3);
        assertThat(adventures).anyMatch(a -> a.title().contains("GR54"));
    }

    @Test
    void getAdventureById() {
        AdventureResponse adventure = adventureService.getById(1L);
        assertThat(adventure.title()).contains("GR54");
        assertThat(adventure.stats()).isNotNull();
        assertThat(adventure.stats().distanceKm()).isNotNull();
        assertThat(adventure.equipment()).isNotEmpty();
    }

    @Test
    void createDraftAdventure() {
        AdventureCreateRequest request = new AdventureCreateRequest(
            "Test Run", LocalDate.now(), "## Test\nContent here.", "TRAIL", 2, List.of(), null, null, null);
        AdventureResponse created = adventureService.create("joffrey", request);
        assertThat(created.id()).isNotNull();
        assertThat(created.status()).isEqualTo("DRAFT");
    }
}
