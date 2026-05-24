package com.adventurekm.backend.controller;

import com.adventurekm.backend.dto.response.AdventureSummaryResponse;
import com.adventurekm.backend.dto.response.UserLevelResponse;
import com.adventurekm.backend.dto.response.UserResponse;
import com.adventurekm.backend.exception.ResourceNotFoundException;
import com.adventurekm.backend.mapper.AdventureMapper;
import com.adventurekm.backend.mapper.UserMapper;
import com.adventurekm.backend.model.AdventureStatus;
import com.adventurekm.backend.model.User;
import com.adventurekm.backend.repository.AdventureRepository;
import com.adventurekm.backend.repository.UserLevelRepository;
import com.adventurekm.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserLevelRepository userLevelRepository;
    private final AdventureRepository adventureRepository;
    private final UserMapper userMapper;
    private final AdventureMapper adventureMapper;

    @GetMapping("/{username}")
    public UserLevelResponse getProfile(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
        return userMapper.toLevelResponse(
                userLevelRepository.findById(user.getId()).orElseThrow());
    }

    @GetMapping("/{username}/adventures")
    public List<AdventureSummaryResponse> getUserAdventures(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
        return adventureMapper.toSummaryResponseList(
                adventureRepository.findByUser_IdAndStatusOrderByDateDesc(
                        user.getId(), AdventureStatus.PUBLISHED));
    }

    @PutMapping("/me/avatar")
    public UserResponse updateAvatar(@AuthenticationPrincipal UserDetails userDetails,
                                     @RequestBody java.util.Map<String, Integer> body) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        user.setAvatarSpriteId(body.get("avatarSpriteId"));
        return userMapper.toResponse(userRepository.save(user));
    }
}
