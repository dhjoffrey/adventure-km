package com.adventurekm.backend.mapper;

import com.adventurekm.backend.dto.response.*;
import com.adventurekm.backend.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface AdventureMapper {

    @Mapping(source = "user", target = "author")
    @Mapping(source = "stats", target = "stats")
    AdventureResponse toResponse(Adventure adventure);

    @Mapping(source = "user", target = "author")
    @Mapping(source = "stats", target = "stats")
    AdventureSummaryResponse toSummaryResponse(Adventure adventure);

    AdventureStatsResponse toStatsResponse(AdventureStats stats);
    PhotoResponse toPhotoResponse(Photo photo);
    EquipmentItemResponse toEquipmentResponse(EquipmentItem item);

    List<AdventureSummaryResponse> toSummaryResponseList(List<Adventure> adventures);
    List<PhotoResponse> toPhotoResponseList(List<Photo> photos);
    List<EquipmentItemResponse> toEquipmentResponseList(Set<EquipmentItem> items);
}
