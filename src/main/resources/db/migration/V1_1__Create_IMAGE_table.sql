create table IMAGE
(
    ID            SERIAL PRIMARY KEY,
    PROJECT_ID    INTEGER REFERENCES PROJECT (ID),
    TAG           VARCHAR(30),
    CREATE_USER   VARCHAR(30),
    CREATE_DATE   DATE,
    IMAGE         VARCHAR(100),
    COMMIT        VARCHAR(400),
    MAJOR_VERSION INTEGER,
    MINOR_VERSION INTEGER,
    INCREMENTAL_VERSION INTEGER,
    BUILD_NUMBER  INTEGER
);

-- INSERT INTO IMAGE
-- VALUES (DEFAULT, 2, '1.0-1', 'System', null, 'etw-docker-03/zus/zus', 1, 0, 1)
