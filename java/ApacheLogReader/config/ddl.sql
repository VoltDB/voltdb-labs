CREATE TABLE log (
    interval_id     INTEGER         NOT NULL,
    time_stamp      TIMESTAMP       NOT NULL,
    log_line        VARCHAR(2000)   NOT NULL
);

CREATE TABLE log_fields(
    interval_id     INTEGER             NOT NULL,
    host            VARCHAR(100)         NOT NULL,
    item_exists     VARCHAR(100)         NOT NULL,
    user_id         VARCHAR(100)         NOT NULL,
    time_stamp      TIMESTAMP            NOT NULL,
    method          VARCHAR(10)          NOT NULL,
    url             VARCHAR(200)         NOT NULL,
    client          VARCHAR(25)          NOT NULL,
    status_code     INTEGER              NOT NULL,
    size            INTEGER              NOT NULL,
    extended        VARCHAR(1000)
);

PARTITION TABLE log ON COLUMN interval_id;
PARTITION TABLE log_fields ON COLUMN interval_id;

CREATE PROCEDURE FROM CLASS com.voltdb.apachelogreader.procedures.InsertLogEntry;

CREATE VIEW bandwidth_stats (
    interval_id,
    url,
    total_assets,
    total_size)
    AS
    SELECT interval_id, url, count(*) as total_assets, sum(size) as total_size 
    FROM log_fields
    GROUP BY interval_id, url;
