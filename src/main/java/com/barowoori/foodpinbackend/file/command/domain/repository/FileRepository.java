package com.barowoori.foodpinbackend.file.command.domain.repository;

import com.barowoori.foodpinbackend.file.command.domain.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, String> {
}
