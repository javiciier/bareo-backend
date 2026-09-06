-- ======================================== ENTITIES ========================================
CREATE TABLE users.user_account
(
    id               VARCHAR(128) PRIMARY KEY,
    username         VARCHAR(50)  NOT NULL,
    email            VARCHAR(255) NOT NULL,
    gender           VARCHAR(20),
    city             VARCHAR(100),
    iso_country_code VARCHAR(2),
    avatar_url       TEXT,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ,

    CONSTRAINT UQ_UserAccount_username UNIQUE (username),
    CONSTRAINT UQ_UserAccount_email UNIQUE (email)
);


-- ======================================== FOREIGN KEYS ========================================


-- ======================================== INDEXING ========================================
