package com.adventurekm.backend.mapper;

import com.adventurekm.backend.dto.response.UserLevelResponse;
import com.adventurekm.backend.dto.response.UserResponse;
import com.adventurekm.backend.model.User;
import com.adventurekm.backend.model.UserLevel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.avatarSpriteId", target = "avatarSpriteId")
    @Mapping(source = "user.theme", target = "theme")
    UserLevelResponse toLevelResponse(UserLevel level);

    List<UserLevelResponse> toLevelResponseList(List<UserLevel> levels);
}
