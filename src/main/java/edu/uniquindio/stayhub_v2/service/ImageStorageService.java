package edu.uniquindio.stayhub_v2.service;

import edu.uniquindio.stayhub_v2.dto.accommodation.ImageResourceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class ImageStorageService {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public ImageResourceDTO save(Long accommodationId, MultipartFile file) {
        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf('.'))
                : ".jpg";

        String id = UUID.randomUUID().toString();
        String filename = id + ext;

        Path dir = Paths.get(uploadDir, "accommodations", String.valueOf(accommodationId));
        try {
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store image", e);
        }

        String url = baseUrl + "/uploads/accommodations/" + accommodationId + "/" + filename;
        log.info("Stored image {} for accommodation {}", filename, accommodationId);
        return new ImageResourceDTO(id, url);
    }

    public void delete(Long accommodationId, String imageId) {
        Path dir = Paths.get(uploadDir, "accommodations", String.valueOf(accommodationId));
        try (var stream = Files.list(dir)) {
            stream.filter(p -> p.getFileName().toString().startsWith(imageId))
                    .findFirst()
                    .ifPresent(p -> {
                        try {
                            Files.deleteIfExists(p);
                            log.info("Deleted image {} for accommodation {}", imageId, accommodationId);
                        } catch (IOException e) {
                            throw new UncheckedIOException("Failed to delete image", e);
                        }
                    });
        } catch (IOException e) {
            log.warn("Could not list upload directory for accommodation {}: {}", accommodationId, e.getMessage());
        }
    }
}
