package com.adventurekm.backend.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record GpxDataResponse(
    BigDecimal distanceKm,
    Integer elevationGainM,
    Integer elevationLossM,
    Integer durationMinutes,
    Integer maxAltitudeM,
    Integer minAltitudeM,
    String geojson,
    List<ElevationPoint> elevationPoints
) {
    public record ElevationPoint(double distanceKm, double altitudeM) {}
}
