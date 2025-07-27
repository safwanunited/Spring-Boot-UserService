-- =======================
-- Table: role
-- =======================
CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(255) NOT NULL
);

-- =======================
-- Table: user
-- =======================
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- =======================
-- Table: user_roles (Join Table for ManyToMany)
-- =======================
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES role(id)
);

-- =======================
-- Table: session
-- =======================
CREATE TABLE session (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(512) NOT NULL,
    expiring_at TIMESTAMP NOT NULL,
    user_id BIGINT,
    session_status INT NOT NULL, -- Enum mapped as ORDINAL
    CONSTRAINT fk_session_user FOREIGN KEY (user_id) REFERENCES user(id)
);
