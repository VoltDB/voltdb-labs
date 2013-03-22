-------------------- EXAMPLE SQL -----------------------------------------------
-- CREATE TABLE example_of_types (
--   id              INTEGER NOT NULL, -- java int, 4-byte signed integer, -2,147,483,647 to 2,147,483,647
--   name            VARCHAR(40),      -- java String
--   data            VARBINARY(256),   -- java byte array 
--   status          TINYINT,          -- java byte, 1-byte signed integer, -127 to 127
--   type            SMALLINT,         -- java short, 2-byte signed integer, -32,767 to 32,767
--   pan             BIGINT,           -- java long, 8-byte signed integer, -9,223,372,036,854,775,807 to 9,223,372,036,854,775,807
--   balance_open    FLOAT,            -- java double, 8-byte numeric
--   balance         DECIMAL,          -- java BigDecimal, 16-byte fixed scale of 12 and precision of 38
--   last_updated    TIMESTAMP,        -- java long, org.voltdb.types.TimestampType, 8-byte signed integer (milliseconds since epoch)
--   CONSTRAINT pk_example_of_types PRIMARY KEY (id)
-- );
-- PARTITION TABLE example_of_types ON COLUMN id;
--
-- CREATE VIEW view_example AS 
--  SELECT type, COUNT(*) AS records, SUM(balance)
--  FROM example_of_types
--  GROUP BY type;
-- 
-- CREATE PROCEDURE FROM CLASS procedures.UpsertSymbol;
-- PARTITION PROCEDURE UpsertSymbol ON TABLE symbols COLUMN symbol PARAMETER 0;
---------------------------------------------------------------------------------

-------------- REPLICATED TABLES ------------------------------------------------

CREATE TABLE inventory (
  inventory_id           INTEGER        NOT NULL,
  site_id                INTEGER       NOT NULL,
  page_id                INTEGER       NOT NULL,
  CONSTRAINT pk_inventory PRIMARY KEY (inventory_id)
);

CREATE TABLE creatives (
  creative_id            INTEGER       NOT NULL,
  campaign_id            INTEGER       NOT NULL,
  advertiser_id          INTEGER       NOT NULL,
  CONSTRAINT pk_creatives PRIMARY KEY (creative_id)
);

-------------- PARTITIONED TABLES ----------------------------------------------

CREATE TABLE impression_data (
  utc_time               TIMESTAMP     NOT NULL,
  ip_address             BIGINT        NOT NULL,
  cookie_uid             BIGINT,
  creative_id            INTEGER       NOT NULL,
  inventory_id           INTEGER       NOT NULL,
  type_id                INTEGER       NOT NULL,
  -- derived from utc_time:
  utc_day                TIMESTAMP     NOT NULL,
  utc_hr                 TIMESTAMP     NOT NULL,
  utc_min                TIMESTAMP     NOT NULL,
  -- derived from creative_id:
  campaign_id            INTEGER       NOT NULL,
  advertiser_id          INTEGER       NOT NULL,
  -- derived from inventory_id:
  site_id                INTEGER       NOT NULL,
  page_id                INTEGER       NOT NULL,
  -- derived from type_id:
  is_impression          INTEGER       NOT NULL,
  is_clickthrough        INTEGER       NOT NULL,
  is_conversion          INTEGER       NOT NULL
);
PARTITION TABLE impression_data ON COLUMN creative_id;

-------------- VIEWS ----------------------------------------------

CREATE VIEW ad_campaign_rates_hourly AS
SELECT advertiser_id, campaign_id, utc_hr, COUNT(*) AS records, SUM(is_impression) AS impressions, SUM(is_clickthrough) AS clicks, SUM(is_conversion) as conversions
FROM impression_data
GROUP BY advertiser_id, campaign_id, utc_hr;

CREATE VIEW ad_campaign_creative_rates_hourly AS
SELECT advertiser_id, campaign_id, creative_id, utc_hr, COUNT(*) AS records, SUM(is_impression) AS impressions, SUM(is_clickthrough) AS clicks, SUM(is_conversion) as conversions
FROM impression_data
GROUP BY advertiser_id, campaign_id, creative_id, utc_hr;


CREATE VIEW ad_campaign_rates_minutely AS
SELECT advertiser_id, campaign_id, utc_min, COUNT(*) AS records, SUM(is_impression) AS impressions, SUM(is_clickthrough) AS clicks, SUM(is_conversion) as conversions
FROM impression_data
GROUP BY advertiser_id, campaign_id, utc_min;

-------------- PROCEDURES -------------------------------------------

CREATE PROCEDURE FROM CLASS procedures.TrackImpression;
PARTITION PROCEDURE TrackImpression ON TABLE impression_data COLUMN creative_id PARAMETER 3;

CREATE PROCEDURE ad_campaign_hourly_rates AS
SELECT campaign_id, utc_hr, clicks/impressions as ctr, conversions/clicks as cr
FROM ad_campaign_rates_hourly
WHERE advertiser_id = ?
ORDER BY campaign_id, utc_hr;

CREATE PROCEDURE ad_campaign_creative_hourly_rates AS
SELECT creative_id, utc_hr, clicks/impressions as ctr, conversions/clicks as cr
FROM ad_campaign_creative_rates_hourly
WHERE advertiser_id = ? AND campaign_id = ?
ORDER BY utc_hr ASC, ctr DESC;


CREATE PROCEDURE ad_campaign_minutely_rates AS
SELECT campaign_id, utc_min, clicks/impressions as ctr, conversions/clicks as cr
FROM ad_campaign_rates_minutely
WHERE advertiser_id = ? AND utc_min > ?
ORDER BY campaign_id, utc_min;
