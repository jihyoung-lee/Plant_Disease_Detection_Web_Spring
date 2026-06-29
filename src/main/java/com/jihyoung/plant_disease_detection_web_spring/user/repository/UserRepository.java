package com.jihyoung.plant_disease_detection_web_spring.user.repository;

import com.jihyoung.plant_disease_detection_web_spring.user.dto.Provider;
import com.jihyoung.plant_disease_detection_web_spring.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);
}
