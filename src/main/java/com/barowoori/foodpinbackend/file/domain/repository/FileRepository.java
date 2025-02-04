package com.barowoori.foodpinbackend.file.domain.repository;

import com.barowoori.foodpinbackend.file.domain.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, String> {
}
