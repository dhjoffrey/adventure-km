package com.adventurekm.backend.repository;

import com.adventurekm.backend.model.EquipmentItem;
import com.adventurekm.backend.model.EquipmentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EquipmentItemRepository extends JpaRepository<EquipmentItem, Long> {
    List<EquipmentItem> findByCategory(EquipmentCategory category);
}
