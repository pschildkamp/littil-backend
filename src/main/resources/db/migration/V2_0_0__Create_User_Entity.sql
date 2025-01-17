CREATE TABLE user
(
    user_id            BINARY(16)   NOT NULL,
    provider           VARCHAR(50)  NOT NULL,
    provider_id        VARCHAR(255),
    email_address      VARCHAR(255) NOT NULL UNIQUE,
    created_by         BINARY(16),
    created_date       DATETIME,
    last_modified_by   BINARY(16),
    last_modified_date DATETIME,
    PRIMARY KEY (user_id),
    CONSTRAINT uc_user_email_address UNIQUE (email_address)
) ENGINE=INNODB;
