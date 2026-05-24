package com.adventurekm.backend.controller;

import com.adventurekm.backend.dto.response.UserLevelResponse;
import com.adventurekm.backend.mapper.UserMapper;
import com.adventurekm.backend.repository.UserLevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final UserLevelRepository userLevelRepository;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserLevelResponse> getLeaderboard(
            @RequestParam(defaultValue = "score") String sortBy) {
        return switch (sortBy) {
            case "km" -> userMapper.toLevelResponseList(userLevelRepository.findAllByOrderByTotalKmDesc());
            case "elevation" -> userMapper.toLevelResponseList(userLevelRepository.findAllByOrderByTotalElevationMDesc());
            case "count" -> userMapper.toLevelResponseList(userLevelRepository.findAllByOrderByAdventureCountDesc());
            default -> userMapper.toLevelResponseList(userLevelRepository.findAllByOrderByRpgScoreDesc());
        };
    }
}
