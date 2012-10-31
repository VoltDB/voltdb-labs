CREATE TABLE DEMOGRAPHIC_AGGREGATION (
    key             varchar(200)        NOT NULL,
    profile_id      varchar(100)        UNIQUE NOT NULL,
    network         varchar(100)        NOT NULL,
    sex             varchar(100)        NOT NULL,
    impressions     integer             NOT NULL,
    marital_status  VARCHAR(100)        NOT NULL,
    income          VARCHAR(100)        NOT NULL,
    education       VARCHAR(100)        NOT NULL,
    occupation      VARCHAR(100)        NOT NULL,
    conversions     integer             NOT NULL,
    CONSTRAINT profile_idx PRIMARY KEY
    (profile_id)
    
);

PARTITION TABLE DEMOGRAPHIC_AGGREGATION ON COLUMN key;


CREATE PROCEDURE FROM CLASS com.voltdb.upsert.procs.UpsertDemographicStats;