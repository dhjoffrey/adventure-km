package com.adventurekm.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "adventure_stats")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AdventureStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adventure_id", unique = true, nullable = false)
    private Adventure adventure;

    @Column(name = "distance_km", precision = 8, scale = 2)
    private BigDecimal distanceKm;

    @Column(name = "elevation_gain_m")
    private Integer elevationGainM;

    @Column(name = "elevation_loss_m")
    private Integer elevationLossM;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "max_altitude_m")
    private Integer maxAltitudeM;

    @Column(name = "min_altitude_m")
    private Integer minAltitudeM;

    @Column(name = "avg_altitude_m")
    private Integer avgAltitudeM;
}
