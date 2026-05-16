package com.besquiros.piercingbysamar.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock S3Client s3Client;
    @InjectMocks StorageService storageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(storageService, "bucket", "piercingbysamar");
        ReflectionTestUtils.setField(storageService, "endpoint", "https://eu2.contabostorage.com");
    }

    @Test
    void upload_withValidImage_shouldReturnPublicUrl() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "anneau.jpg", "image/jpeg", new byte[1024]);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(null);

        String url = storageService.upload(file, "products/1");

        assertThat(url).startsWith("https://eu2.contabostorage.com/piercingbysamar/products/1/");
        assertThat(url).endsWith(".jpg");
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void upload_withEmptyFile_shouldThrowException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.jpg", "image/jpeg", new byte[0]);

        assertThatThrownBy(() -> storageService.upload(file, "products/1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("vide");
    }

    @Test
    void upload_withNonImageContentType_shouldThrowException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", new byte[1024]);

        assertThatThrownBy(() -> storageService.upload(file, "products/1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("images");
    }

    @Test
    void upload_withFileTooLarge_shouldThrowException() {
        byte[] bigFile = new byte[6 * 1024 * 1024]; // 6 MB
        MockMultipartFile file = new MockMultipartFile(
                "file", "big.jpg", "image/jpeg", bigFile);

        assertThatThrownBy(() -> storageService.upload(file, "products/1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("5 MB");
    }

    @Test
    void delete_withValidUrl_shouldCallS3Delete() {
        String url = "https://eu2.contabostorage.com/piercingbysamar/products/1/uuid.jpg";

        storageService.delete(url);

        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void delete_withUnknownUrl_shouldNotCallS3() {
        storageService.delete("https://other-provider.com/image.jpg");

        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
    }
}
