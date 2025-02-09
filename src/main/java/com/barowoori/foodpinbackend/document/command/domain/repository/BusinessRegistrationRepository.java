package com.barowoori.foodpinbackend.document.command.domain.repository;

import com.barowoori.foodpinbackend.document.command.domain.model.BusinessRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessRegistrationRepository extends JpaRepository<BusinessRegistration, String> {
}
