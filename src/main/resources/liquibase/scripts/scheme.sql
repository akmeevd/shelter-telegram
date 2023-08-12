-- liquibase formatted sql

-- changeSet 11th:1
CREATE TABLE IF NOT EXISTS users
(
    id          BIGSERIAL PRIMARY KEY NOT NULL,
    telegram_id BIGINT                NOT NULL,
    name        VARCHAR(50),
    surname     VARCHAR(50),
    phone       VARCHAR(50),
    email       VARCHAR(50),
    is_volunteer BOOL,
    state VARCHAR(10),
    days_for_test INT,
    end_test TIMESTAMP,
    volunteer   bool
);

CREATE TABLE IF NOT EXISTS animals
(
    id            BIGSERIAL PRIMARY KEY NOT NULL,
    name          VARCHAR(50)           NOT NULL,
    breed         VARCHAR(50),
    description   TEXT,
    photo         OID,
    user_id       BIGINT REFERENCES users (id),
    state VARCHAR(10),
    type varchar(10),
    animal_type varchar(10)
);
--changeSet slyubimov:1
CREATE TABLE IF NOT EXISTS cats
(
    id            BIGSERIAL PRIMARY KEY NOT NULL,
    name          VARCHAR(50)           NOT NULL,
    breed         VARCHAR(50),
    description   TEXT,
    photo         OID,
    user_id       BIGINT REFERENCES users (id),
    state        VARCHAR(10),
    start_test    TIMESTAMP,
    animal_type VARCHAR(10)
);
alter table users add column animal_id bigint references animals (id);

CREATE TABLE IF NOT EXISTS dogs
(
    id            BIGSERIAL PRIMARY KEY NOT NULL,
    name          VARCHAR(50)           NOT NULL,
    breed         VARCHAR(50),
    description   TEXT,
    photo         OID,
    user_id       BIGINT REFERENCES users (id),
    state        VARCHAR(10),
    start_test    TIMESTAMP,
    animal_type VARCHAR(10)
);


CREATE TABLE IF NOT EXISTS reports
(
    id              BIGSERIAL PRIMARY KEY          NOT NULL,
    user_id         BIGINT REFERENCES users (id)   NOT NULL,
    animal_id       BIGINT REFERENCES animals (id) NOT NULL,
    date            TIMESTAMP                      NOT NULL,
    photo           OID,
    diet            TEXT,
    well_being      TEXT,
    change_behavior TEXT,
    cat_id BIGINT REFERENCES cats(id),
    dog_id BIGINT REFERENCES dogs(id)
);



-- changeSet slyubimov:2
CREATE TABLE hibernate_sequences (
                                     sequence_name varchar(255) NOT NULL,
                                     next_val bigint,
                                     PRIMARY KEY (sequence_name),
                                     sequence_next_hi_value bigint);




