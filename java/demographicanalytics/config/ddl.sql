CREATE TABLE log (
    interval_id     INTEGER         NOT NULL,
    network         VARCHAR(100)    NOT NULL,
    cost            INTEGER         NOT NULL,
    first_name      VARCHAR(100)    NOT NULL,
    last_name       VARCHAR(100)    NOT NULL,
    age_actual      INTEGER         NOT NULL,
    income_actual   INTEGER         NOT NULL,
    age             VARCHAR(100)    NOT NULL,
    income          VARCHAR(100)    NOT NULL,
    sex             VARCHAR(100)    NOT NULL,
    marital_status  VARCHAR(100)    NOT NULL,
    education       VARCHAR(100)    NOT NULL,
    occupation      VARCHAR(100)    NOT NULL,
    conversion      INTEGER         NOT NULL
    
);

CREATE TABLE demographic_aggregation(
    profile_key     VARCHAR(200)         UNIQUE NOT NULL,
    network         VARCHAR(100)         NOT NULL,
    sex             VARCHAR(100)         NOT NULL,
    age             VARCHAR(100)         NOT NULL,
    marital_status  VARCHAR(100)         NOT NULL,
    income          VARCHAR(100)         NOT NULL,
    education       VARCHAR(100)         NOT NULL,
    occupation      VARCHAR(100)         NOT NULL,
    cost            INTEGER         NOT NULL,
    impressions     INTEGER              NOT NULL,
    conversions     INTEGER              NOT NULL,
    CONSTRAINT demographic_aggregation_idx PRIMARY KEY
    (profile_key)
);

CREATE TABLE log_highwater(
    interval_id     INTEGER         NOT NULL,
    network         VARCHAR(100)    NOT NULL,
    CONSTRAINT log_highwater_idx PRIMARY KEY
    (network)
);

PARTITION TABLE log ON COLUMN network;
PARTITION TABLE log_highwater ON COLUMN network;
PARTITION TABLE demographic_aggregation ON COLUMN network;

CREATE INDEX log_interval_idx ON log(interval_id,network);
CREATE INDEX user_idx ON log(first_name, last_name);

CREATE PROCEDURE FROM CLASS com.voltdb.demographicanalytics.procedures.InsertLogEntry;

CREATE VIEW network_impression_stats (
    interval_id,
    network,
    impressions,
    conversions )
    AS
    SELECT interval_id, network, COUNT(*) as impressions, SUM(conversion) as conversions
    FROM log
    GROUP BY interval_id, network;

CREATE VIEW sex_impression_stats (
    interval_id,
    network,
    sex,
    impressions,
    conversions )
    AS
    SELECT interval_id,  network, sex, COUNT(*) as impressions, SUM(conversion) as conversions
    FROM log
    GROUP BY interval_id, network, sex;
    
CREATE VIEW age_impression_stats (
    interval_id,
    network,
    age,
    impressions,
    conversions )
    AS
    SELECT interval_id, network, age, COUNT(*) as impressions, SUM(conversion) as conversions
    FROM log
    GROUP BY interval_id, network, age;
    
CREATE VIEW marital_status_impression_stats (
    interval_id,
    network,
    marital_status,
    impressions,
    conversions )
    AS
    SELECT interval_id, network, marital_status, COUNT(*) as impressions, SUM(conversion) as conversions
    FROM log
    GROUP BY interval_id, network, marital_status;
    
CREATE VIEW income_impression_stats (
    interval_id,
    network,
    income,
    impressions,
    conversions )
    AS
    SELECT interval_id, network, income, COUNT(*) as impressions, SUM(conversion) as conversions
    FROM log
    GROUP BY interval_id, network, income;
    
CREATE VIEW education_impression_stats (
    interval_id,
    network,
    education,
    impressions,
    conversions )
    AS
    SELECT interval_id, network, education,  COUNT(*) as impressions, SUM(conversion) as conversions
    FROM log
    GROUP BY interval_id, network, education;
    
CREATE VIEW occupation_impression_stats (
    interval_id,
    network,
    occupation,
    impressions,
    conversions )
    AS
    SELECT interval_id, network, occupation, COUNT(*) as impressions, SUM(conversion) as conversions
    FROM log
    GROUP BY interval_id, network, occupation;