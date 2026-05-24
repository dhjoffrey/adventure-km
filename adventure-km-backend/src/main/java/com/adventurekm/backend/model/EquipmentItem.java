package com.adventurekm.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "equipment_items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EquipmentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EquipmentCategory category;

    @Column(name = "icon_key", length = 50)
    private String iconKey;

    @Column(name = "pixel_sprite_key", length = 50)
    private String pixelSpriteKey;
}
