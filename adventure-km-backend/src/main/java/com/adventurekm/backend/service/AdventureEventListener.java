package com.adventurekm.backend.service;

import com.adventurekm.backend.model.Adventure;
import com.adventurekm.backend.model.AdventurePublishedEvent;
import com.adventurekm.backend.model.AdventureStatus;
import com.adventurekm.backend.model.User;
import com.adventurekm.backend.model.UserLevel;
import com.adventurekm.backend.repository.AdventureRepository;
import com.adventurekm.backend.repository.UserLevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AdventureEventListener {

    private final AdventureRepository adventureRepository;
    private final UserLevelRepository userLevelRepository;
    private final LevelCalculationService levelCalculationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onAdventurePublished(AdventurePublishedEvent event) {
        Long userId = event.adventure().getUser().getId();

        List<Adventure> published = adventureRepository
                .findByUser_IdAndStatusOrderByDateDesc(userId, AdventureStatus.PUBLISHED);

        BigDecimal totalKm = BigDecimal.ZERO;
        int totalElevation = 0;
        int count = published.size();

        for (Adventure a : published) {
            if (a.getStats() != null) {
                if (a.getStats().getDistanceKm() != null)
                    totalKm = totalKm.add(a.getStats().getDistanceKm());
                if (a.getStats().getElevationGainM() != null)
                    totalElevation += a.getStats().getElevationGainM();
            }
        }

        int score = levelCalculationService.calculateScore(totalKm, totalElevation, count);
        int level = levelCalculationService.calculateLevel(score);

        UserLevel userLevel = userLevelRepository.findById(userId)
                .orElseGet(() -> {
                    User user = event.adventure().getUser();
                    return userLevelRepository.save(UserLevel.builder().user(user).build());
                });
        userLevel.setTotalKm(totalKm);
        userLevel.setTotalElevationM(totalElevation);
        userLevel.setAdventureCount(count);
        userLevel.setRpgScore(score);
        userLevel.setLevel(level);
        userLevelRepository.save(userLevel);
    }
}
