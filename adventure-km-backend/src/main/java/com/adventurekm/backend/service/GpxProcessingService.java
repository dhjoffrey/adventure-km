package com.adventurekm.backend.service;

import com.adventurekm.backend.dto.response.GpxDataResponse;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.geom.Geoid;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GpxProcessingService {

    public GpxDataResponse process(InputStream inputStream) {
        try {
            GPX gpx = GPX.Reader.DEFAULT.read(inputStream);

            List<WayPoint> points = gpx.tracks()
                    .flatMap(Track::segments)
                    .flatMap(TrackSegment::points)
                    .toList();

            if (points.isEmpty()) {
                throw new IllegalArgumentException("GPX file contains no track points");
            }

            // Compute cumulative distances, elevation stats, and elevation profile
            double totalDistanceMeters = 0.0;
            int elevationGain = 0;
            int elevationLoss = 0;
            double maxAlt = Double.NEGATIVE_INFINITY;
            double minAlt = Double.POSITIVE_INFINITY;

            List<GpxDataResponse.ElevationPoint> elevationPoints = new ArrayList<>();

            // GeoJSON coordinates array builder
            StringBuilder coordsBuilder = new StringBuilder();

            for (int i = 0; i < points.size(); i++) {
                WayPoint p = points.get(i);
                double lat = p.getLatitude().doubleValue();
                double lon = p.getLongitude().doubleValue();
                double ele = p.getElevation()
                        .map(io.jenetics.jpx.Length::doubleValue)
                        .orElse(0.0);

                // Track altitude bounds
                if (ele > maxAlt) maxAlt = ele;
                if (ele < minAlt) minAlt = ele;

                // Cumulative distance
                if (i > 0) {
                    WayPoint prev = points.get(i - 1);
                    double segmentMeters = Geoid.WGS84.distance(prev, p).doubleValue();
                    totalDistanceMeters += segmentMeters;

                    // Elevation gain/loss
                    double prevEle = prev.getElevation()
                            .map(io.jenetics.jpx.Length::doubleValue)
                            .orElse(0.0);
                    double diff = ele - prevEle;
                    if (diff > 0) {
                        elevationGain += (int) diff;
                    } else {
                        elevationLoss += (int) Math.abs(diff);
                    }
                }

                double cumDistKm = totalDistanceMeters / 1000.0;
                elevationPoints.add(new GpxDataResponse.ElevationPoint(cumDistKm, ele));

                // GeoJSON coordinate: [lon, lat, ele]
                if (i > 0) coordsBuilder.append(",");
                coordsBuilder.append("[")
                        .append(lon).append(",")
                        .append(lat).append(",")
                        .append(ele)
                        .append("]");
            }

            // Duration
            long durationMinutes = 0;
            Optional<Instant> firstTime = points.get(0).getTime();
            Optional<Instant> lastTime = points.get(points.size() - 1).getTime();
            if (firstTime.isPresent() && lastTime.isPresent()) {
                durationMinutes = Duration.between(firstTime.get(), lastTime.get()).toMinutes();
            }

            // GeoJSON feature
            String geojson = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":["
                    + coordsBuilder
                    + "]},\"properties\":{}}";

            BigDecimal distanceKm = BigDecimal.valueOf(totalDistanceMeters / 1000.0)
                    .setScale(2, RoundingMode.HALF_UP);

            return new GpxDataResponse(
                    distanceKm,
                    elevationGain,
                    elevationLoss,
                    (int) durationMinutes,
                    (int) Math.round(maxAlt),
                    (int) Math.round(minAlt),
                    geojson,
                    elevationPoints
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to process GPX file: " + e.getMessage(), e);
        }
    }
}
