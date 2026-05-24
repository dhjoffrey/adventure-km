package com.adventurekm.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService(tempDir.toString());
    }

    @Test
    void saveAndLoadPhoto() throws Exception {
        byte[] imageBytes = createMinimalJpeg();
        MockMultipartFile file = new MockMultipartFile("photo", "test.jpg", "image/jpeg", imageBytes);

        String savedPath = fileStorageService.savePhoto(1L, file, 1);
        assertThat(savedPath).contains("1");
        assertThat(Files.exists(Path.of(tempDir.toString(), savedPath))).isTrue();
    }

    @Test
    void saveGpxFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("gpx", "track.gpx", "application/gpx+xml", "<gpx/>".getBytes());
        String savedPath = fileStorageService.saveGpx(1L, file);
        assertThat(savedPath).endsWith(".gpx");
        assertThat(Files.exists(Path.of(tempDir.toString(), savedPath))).isTrue();
    }

    private byte[] createMinimalJpeg() throws Exception {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos);
        return baos.toByteArray();
    }
}
