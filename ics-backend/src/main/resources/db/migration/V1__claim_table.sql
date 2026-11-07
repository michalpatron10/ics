CREATE SEQUENCE IF NOT EXISTS claim_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE claim
(
    id                BIGINT NOT NULL,
    claim_number      VARCHAR(255),
    claim_status      VARCHAR(255),
    claim_type        VARCHAR(255),
    claim_description VARCHAR(255),
    claim_date        TIMESTAMP(6) WITHOUT TIME ZONE,

    CONSTRAINT pk_claim PRIMARY KEY (id)
);