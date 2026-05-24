package com.adventurekm.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "user_levels")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserLevel {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "total_km", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalKm = BigDecimal.ZERO;

    @Column(name = "total_elevation_m")
    @Builder.Default
    private Integer totalElevationM = 0;

    @Column(name = "adventure_count")
    @Builder.Default
    private Integer adventureCount = 0;

    @Column(name = "rpg_score")
    @Builder.Default
    private Integer rpgScore = 0;

    @Builder.Default
    private Integer level = 1;
}
