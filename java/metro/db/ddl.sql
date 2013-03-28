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
-- CREATE INDEX pan_example ON example_of_types (pan);
--
-- CREATE VIEW view_example AS 
--  SELECT type, COUNT(*) AS records, SUM(balance)
--  FROM example_of_types
--  GROUP BY type;
-- 
-- CREATE PROCEDURE foo AS SELECT * FROM foo;
-- CREATE PROCEDURE FROM CLASS procedures.UpsertSymbol;
-- PARTITION PROCEDURE UpsertSymbol ON TABLE symbols COLUMN symbol PARAMETER 0;
---------------------------------------------------------------------------------

------------- REPLICATED TABLES --------------

------------- PARTITIONED TABLES -------------
CREATE TABLE metrocards(
  card_id		INTEGER        NOT NULL,
  card_status		VARCHAR(8)     NOT NULL,
  CONSTRAINT PK_metrocards_card_id PRIMARY KEY ( card_id )
);
PARTITION TABLE metrocards ON COLUMN card_id;

CREATE TABLE card_swipes(
  card_id		INTEGER        NOT NULL,
  date_time		TIMESTAMP      NOT NULL,
  date_int		INTEGER	       NOT NULL,
  hour_int		INTEGER	       NOT NULL,
  location_id		INTEGER	       NOT NULL,
  is_green		TINYINT,
  is_red		TINYINT  
);
PARTITION TABLE card_swipes ON COLUMN card_id;

----------------- PROCEDURES ------------------

CREATE PROCEDURE FROM CLASS procedures.CardSwipe;
PARTITION PROCEDURE CardSwipe ON TABLE card_swipes COLUMN card_id PARAMETER 0;

----------------- VIEWS -----------------------

CREATE VIEW hourly_swipes_by_location(
  location_id,
  date_int,
  hour_int,
  total_swipes,
  total_green,
  total_red
) AS
SELECT
  location_id,
  date_int,
  hour_int,
  COUNT(*),
  SUM(is_green),
  SUM(is_red)
FROM card_swipes
GROUP BY
  location_id,
  date_int,
  hour_int;
