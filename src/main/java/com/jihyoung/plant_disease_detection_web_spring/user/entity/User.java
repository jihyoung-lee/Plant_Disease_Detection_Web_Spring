package com.jihyoung.plant_disease_detection_web_spring.user.entity;

import com.jihyoung.plant_disease_detection_web_spring.user.dto.Provider;
import com.jihyoung.plant_disease_detection_web_spring.user.dto.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(
        name = "users"
)
@Getter
// JPA가 객체를 생성할 수 있도록 기본 생성자는 제공하되, 개발자가 실수로 의미 없는 객체를 만들지 못하게 하기 위해 protected로 제한하는 것
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 구글에서 주는 고유 ID
    @Column(nullable = false, unique = true, name = "provider_id")
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Column(nullable = false)
    private String email;

    private String name;

    @Column(nullable = false, name = "profile_image_url")
    private String profileImageUrl;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public User(String providerId, Provider provider, String email, String name, String profileImageUrl) {
        this.providerId = providerId;
        this.provider = provider;
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.role = Role.USER;
    }
}
