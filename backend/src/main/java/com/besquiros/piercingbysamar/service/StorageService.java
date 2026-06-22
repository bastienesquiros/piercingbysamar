package com.besquiros.piercingbysamar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final S3Client s3Client;

    @Value("${storage.bucket}")
    private String bucket;

    @Value("${storage.endpoint}")
    private String endpoint;

    @Value("${storage.public-url}")
    private String publicUrl;

    /**
     * Upload un fichier image vers le bucket Contabo S3.
     * Retourne l'URL publique de l'image.
     */
    public String upload(MultipartFile file, String folder) {
        validateImageFile(file);

        String extension = getExtension(file.getOriginalFilename());
        String key = folder + "/" + UUID.randomUUID() + "." + extension;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            String url = publicUrl + "/" + key;
            log.info("Image uploadée : {}", url);
            return url;

        } catch (IOException e) {
            log.error("Erreur lors de la lecture du fichier : {}", e.getMessage());
            throw new RuntimeException("Erreur lors de l'upload de l'image", e);
        }
    }

    /**
     * Supprime un fichier du bucket à partir de son URL publique.
     */
    public void delete(String url) {
        // Extraire la clé depuis l'URL publique : publicUrl/key
        String prefix = publicUrl + "/";
        if (!url.startsWith(prefix)) {
            log.warn("URL inconnue, suppression ignorée : {}", url);
            return;
        }
        String key = url.substring(prefix.length());

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());

        log.info("Image supprimée : {}", key);
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Seules les images sont acceptées (JPEG, PNG, WebP)");
        }
        // Max 5 MB
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("L'image ne doit pas dépasser 5 MB");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
