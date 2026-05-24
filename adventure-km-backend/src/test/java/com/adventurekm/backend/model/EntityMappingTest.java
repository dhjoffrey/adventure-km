package com.adventurekm.backend.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureDataSourceInitialization;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@AutoConfigureDataSourceInitialization
@ActiveProfiles("dev")
class EntityMappingTest {

    @Autowired
    private TestEntityManager em;

    @Test
    void userAndAdventureMapping() {
        User user = em.find(User.class, 1L);
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("joffrey");
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void adventureWithStatsMapping() {
        Adventure adventure = em.find(Adventure.class, 1L);
        assertThat(adventure).isNotNull();
        assertThat(adventure.getTitle()).contains("GR54");
        assertThat(adventure.getStats()).isNotNull();
        assertThat(adventure.getStats().getElevationGainM()).isEqualTo(12000);
    }

    @Test
    void userLevelMapping() {
        UserLevel level = em.find(UserLevel.class, 1L);
        assertThat(level).isNotNull();
        assertThat(level.getAdventureCount()).isEqualTo(3);
    }

    @Test
    void equipmentLinkMapping() {
        Adventure adventure = em.find(Adventure.class, 1L);
        assertThat(adventure.getEquipment()).isNotEmpty();
        assertThat(adventure.getEquipment().size()).isEqualTo(8);
    }
}
