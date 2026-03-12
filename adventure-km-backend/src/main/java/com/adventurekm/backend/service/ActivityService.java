package com.adventurekm.backend.service;

import com.adventurekm.backend.model.Activity;
import com.adventurekm.backend.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository itemRepository) {
        this.activityRepository = itemRepository;
    }

    public List<Activity> getAllItems() {
        return activityRepository.findAll();
    }
}