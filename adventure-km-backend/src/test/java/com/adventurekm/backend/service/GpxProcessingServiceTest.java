package com.adventurekm.backend.service;

import com.adventurekm.backend.dto.response.GpxDataResponse;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class GpxProcessingServiceTest {

    private final GpxProcessingService service = new GpxProcessingService();

    @Test
    void parseGpxExtractsStats() {
        InputStream gpx = getClass().getResourceAsStream("/test-track.gpx");
        GpxDataResponse data = service.process(gpx);

        assertThat(data.distanceKm()).isGreaterThan(BigDecimal.ZERO);
        assertThat(data.elevationGainM()).isGreaterThan(0);
        assertThat(data.elevationLossM()).isGreaterThan(0);
        assertThat(data.maxAltitudeM()).isEqualTo(600);
        assertThat(data.minAltitudeM()).isEqualTo(500);
        assertThat(data.durationMinutes()).isEqualTo(45);
        assertThat(data.geojson()).contains("coordinates");
        assertThat(data.elevationPoints()).hasSize(5);
    }
}
