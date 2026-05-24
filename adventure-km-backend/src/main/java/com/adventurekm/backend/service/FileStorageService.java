package com.adventurekm.backend.service;

import com.adventurekm.backend.exception.BadRequestException;
import com.adventurekm.backend.exception.ForbiddenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileStorageService {

    private final String uploadDir;

    public FileStorageService(@Value("${app.upload.dir}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String savePhoto(Long adventureId, MultipartFile file, int sortOrder) {
        validateImageFile(file);
        String relativePath = "photos/" + adventureId + "/" + sortOrder + ".jpg";
        Path targetPath = Path.of(uploadDir, relativePath);

        try {
            Files.createDirectories(targetPath.getParent());
            BufferedImage original = ImageIO.read(file.getInputStream());
            if (original == null) {
                throw new BadRequestException("Cannot read image file");
            }
            BufferedImage resized = resizeImage(original, 1200, 800);
            try (OutputStream os = Files.newOutputStream(targetPath)) {
                ImageIO.write(resized, "jpg", os);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save photo", e);
        }

        return relativePath;
    }

    public String saveGpx(Long adventureId, MultipartFile file) {
        String relativePath = "gpx/" + adventureId + ".gpx";
        Path targetPath = Path.of(uploadDir, relativePath);

        try {
            Files.createDirectories(targetPath.getParent());
            file.transferTo(targetPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save GPX file", e);
        }

        return relativePath;
    }

    public Path resolve(String relativePath) {
        Path base = Path.of(uploadDir).toAbsolutePath().normalize();
        Path resolved = base.resolve(relativePath).normalize();
        if (!resolved.startsWith(base)) {
            throw new ForbiddenException("Access denied");
        }
        return resolved;
    }

    private BufferedImage resizeImage(BufferedImage original, int maxWidth, int maxHeight) {
        int w = original.getWidth();
        int h = original.getHeight();
        if (w <= maxWidth && h <= maxHeight) return original;

        double ratio = Math.min((double) maxWidth / w, (double) maxHeight / h);
        int newW = (int) (w * ratio);
        int newH = (int) (h * ratio);

        BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, newW, newH, null);
        g.dispose();
        return resized;
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) throw new BadRequestException("File is empty");
        if (file.getSize() > 10 * 1024 * 1024) throw new BadRequestException("File exceeds 10 MB limit");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("File must be an image");
        }
    }
}
