CREATE TABLE users
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    provider_id       VARCHAR(255) NOT NULL,
    provider          VARCHAR(255) NOT NULL,
    email             VARCHAR(255) NOT NULL,
    name              VARCHAR(255) NULL,
    profile_image_url VARCHAR(255) NOT NULL,
    `role`            VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_provider UNIQUE (provider_id);