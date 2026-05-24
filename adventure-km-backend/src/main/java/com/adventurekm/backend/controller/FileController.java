package com.adventurekm.backend.controller;

import com.adventurekm.backend.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping("/photos/{adventureId}/{filename}")
    public Resource servePhoto(@PathVariable Long adventureId, @PathVariable String filename) {
        Path path = fileStorageService.resolve("photos/" + adventureId + "/" + filename);
        return new FileSystemResource(path);
    }

    @GetMapping(value = "/gpx/{adventureId}.gpx", produces = "application/gpx+xml")
    public Resource serveGpx(@PathVariable Long adventureId) {
        Path path = fileStorageService.resolve("gpx/" + adventureId + ".gpx");
        return new FileSystemResource(path);
    }
}
