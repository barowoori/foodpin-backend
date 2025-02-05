package com.barowoori.foodpinbackend.member.infra.domain;

import com.barowoori.foodpinbackend.file.infra.domain.ImageDirectory;
import com.barowoori.foodpinbackend.file.infra.domain.S3ImageManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class S3ImageManagerTests {
    @Autowired
    private S3ImageManager s3ImageManager;

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("프로필 업로드 테스트")
    class UpdateProfile {
        @Test
        @DisplayName("지정된 프로필이 없을 때 업로드하면 업로드한 파일이 s3에 업로드된다")
        void WhenNotExistOldProfile() throws IOException {

            String fileName = "test-file.jpg";
            byte[] fileContent = "test-content".getBytes();
            InputStream fileInputStream = new ByteArrayInputStream(fileContent);

            when(multipartFile.getOriginalFilename()).thenReturn(fileName);
            when(multipartFile.getBytes()).thenReturn(fileContent);
            when(multipartFile.getInputStream()).thenReturn(fileInputStream);

            String uploadedUrl = s3ImageManager.updateFile(multipartFile, null, ImageDirectory.PROFILE);

            assertNotNull(uploadedUrl);
            System.out.println(uploadedUrl);
        }

        @Test
        @DisplayName("지정된 프로필이 있을 때 업로드하면 기존 프로필은 s3에서 삭제되고 업로드한 파일이 s3에 업로드된다")
        void WhenExistOldProfile() throws IOException {

            String fileName = "test-file.jpg";
            byte[] fileContent = "test-content".getBytes();
            InputStream fileInputStream = new ByteArrayInputStream(fileContent);

            when(multipartFile.getOriginalFilename()).thenReturn(fileName);
            when(multipartFile.getBytes()).thenReturn(fileContent);
            when(multipartFile.getInputStream()).thenReturn(fileInputStream);
            String oldUploadedUrl = s3ImageManager.updateFile(multipartFile, null, ImageDirectory.PROFILE);

            String uploadedUrl = s3ImageManager.updateFile(multipartFile, oldUploadedUrl, ImageDirectory.PROFILE);
            assertNotEquals(oldUploadedUrl, uploadedUrl);
            assertNotNull(uploadedUrl);
            System.out.println(uploadedUrl);
        }
    }


}
