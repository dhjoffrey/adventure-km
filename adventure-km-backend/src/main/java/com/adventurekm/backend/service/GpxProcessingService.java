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
        GPX gpx;
        List<WayPoint> points;
        try {
            gpx = GPX.Reader.DEFAULT.read(inputStream);
            points = gpx.tracks()
                    .flatMap(Track::segments)
                    .flatMap(TrackSegment::points)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse GPX file", e);
        }

        if (points.isEmpty()) {
            throw new IllegalArgumentException("GPX file contains no track points");
        }

        // Compute cumulative distances, elevation stats, and elevation profile
        double totalDistanceMeters = 0.0;
        int elevationGain = 0;
        int elevationLoss = 0;
        double maxAlt = Double.NEGATIVE_INFINITY;
        double minAlt = Double.POSITIVE_INFINITY;
        double sumAlt = 0.0;

        List<GpxDataResponse.ElevationPoint> elevationPoints = new ArrayList<>();
        // [lat, lon, cumDistKm] — used to match waypoints to track distance
        List<double[]> trackCoords = new ArrayList<>();

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
            sumAlt += ele;

            // Cumulative distance
            if (i > 0) {
                WayPoint prev = points.get(i - 1);
                double segmentMeters = Geoid.WGS84.distance(prev, p).doubleValue();
                totalDistanceMeters += segmentMeters;

                // Elevation gain/loss — 5m threshold to filter GPS noise
                double prevEle = prev.getElevation()
                        .map(io.jenetics.jpx.Length::doubleValue)
                        .orElse(0.0);
                double diff = ele - prevEle;
                if (diff > 5.0) {
                    elevationGain += (int) diff;
                } else if (diff < -5.0) {
                    elevationLoss += (int) Math.abs(diff);
                }
            }

            double cumDistKm = totalDistanceMeters / 1000.0;
            elevationPoints.add(new GpxDataResponse.ElevationPoint(cumDistKm, ele));
            trackCoords.add(new double[]{lat, lon, cumDistKm});

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

        // Extract <wpt> elements and project them onto the track distance
        List<GpxDataResponse.WaypointDto> waypoints = gpx.wayPoints().map(wpt -> {
            double wLat = wpt.getLatitude().doubleValue();
            double wLon = wpt.getLongitude().doubleValue();
            double wAlt = wpt.getElevation().map(io.jenetics.jpx.Length::doubleValue).orElse(0.0);
            String name = wpt.getName().orElse("");
            double bestCumDist = 0.0;
            double bestDistSq = Double.MAX_VALUE;
            for (double[] tc : trackCoords) {
                double dlat = tc[0] - wLat;
                double dlon = tc[1] - wLon;
                double dSq = dlat * dlat + dlon * dlon;
                if (dSq < bestDistSq) {
                    bestDistSq = dSq;
                    bestCumDist = tc[2];
                }
            }
            return new GpxDataResponse.WaypointDto(name, wLat, wLon, wAlt, bestCumDist);
        }).toList();

        BigDecimal distanceKm = BigDecimal.valueOf(totalDistanceMeters / 1000.0)
                .setScale(2, RoundingMode.HALF_UP);

        int avgAlt = (int) Math.round(sumAlt / points.size());

        return new GpxDataResponse(
                distanceKm,
                elevationGain,
                elevationLoss,
                (int) durationMinutes,
                (int) Math.round(maxAlt),
                (int) Math.round(minAlt),
                avgAlt,
                geojson,
                elevationPoints,
                waypoints
        );
    }
}
