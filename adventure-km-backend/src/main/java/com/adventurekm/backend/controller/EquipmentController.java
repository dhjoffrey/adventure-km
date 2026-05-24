package com.adventurekm.backend.controller;

import com.adventurekm.backend.dto.response.EquipmentItemResponse;
import com.adventurekm.backend.mapper.AdventureMapper;
import com.adventurekm.backend.repository.EquipmentItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentItemRepository equipmentItemRepository;
    private final AdventureMapper adventureMapper;

    @GetMapping
    public List<EquipmentItemResponse> listEquipment() {
        return equipmentItemRepository.findAll().stream()
                .map(adventureMapper::toEquipmentResponse).toList();
    }
}
