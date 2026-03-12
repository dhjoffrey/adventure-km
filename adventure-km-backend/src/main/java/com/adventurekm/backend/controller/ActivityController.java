package com.adventurekm.backend.controller;

import com.adventurekm.backend.model.Activity;
import com.adventurekm.backend.service.ActivityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:4200")  // Angular
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public List<Activity> getItems() {
        return activityService.getAllItems();
    }
}