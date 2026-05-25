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
    Integer avgAltitudeM,
    String geojson,
    List<ElevationPoint> elevationPoints,
    List<WaypointDto> waypoints
) {
    public record ElevationPoint(double distanceKm, double altitudeM) {}
    public record WaypointDto(String name, double lat, double lon, double altM, double distanceKm) {}
}
