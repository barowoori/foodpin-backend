package com.barowoori.foodpinbackend.file.command.domain.model;

import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@Getter
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "path", length = 500)
    private String path;

    protected File(){}

    @Builder
    public File(String path) {
        this.path = path;
    }

    public String getPreSignUrl(ImageManager imageManager){
        return imageManager.getPreSignUrl(this.path);
    }

    public String getFileName() {
        if (this.path == null || this.path.isEmpty()) {
            return null;
        }

        try {
            URL url = new URL(this.path);
            String filePath = url.getPath();
            String fileNameEncoded = filePath.substring(filePath.lastIndexOf('/') + 1);
            return URLDecoder.decode(fileNameEncoded, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return null;
        }
    }
}
