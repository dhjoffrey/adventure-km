package com.adventurekm.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "adventures")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Adventure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AdventureType type;

    private Integer difficulty;

    @Column(name = "gpx_path", length = 500)
    private String gpxPath;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Builder.Default
    private AdventureStatus status = AdventureStatus.DRAFT;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "adventure", cascade = CascadeType.ALL, orphanRemoval = true,
              fetch = FetchType.LAZY)
    private AdventureStats stats;

    @OneToMany(mappedBy = "adventure", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder")
    @Builder.Default
    private List<Photo> photos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "adventure_equipment",
        joinColumns = @JoinColumn(name = "adventure_id"),
        inverseJoinColumns = @JoinColumn(name = "equipment_id")
    )
    @Builder.Default
    private Set<EquipmentItem> equipment = new HashSet<>();
}
