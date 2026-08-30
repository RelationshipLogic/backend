ALTER TABLE users
    ADD COLUMN provider VARCHAR(20) NOT NULL,
    ADD COLUMN provider_id VARCHAR(255) NOT NULL,
    ADD COLUMN email VARCHAR(255),
    ADD COLUMN nickname VARCHAR(100);

ALTER TABLE users
    ADD CONSTRAINT uk_users_provider_provider_id UNIQUE (provider, provider_id);
