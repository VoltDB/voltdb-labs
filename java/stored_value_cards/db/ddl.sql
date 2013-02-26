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

------------- PARTITIONED TABLES --------------

CREATE TABLE card_account(
  pan   		VARCHAR(16)    NOT NULL,
  card_available	INTEGER        NOT NULL, -- 1=ACTIVE, 0=INACTIVE
  card_status		VARCHAR(20)    NOT NULL, -- CREATED, PRINTED, WAREHOUSED, SHIPPED, ACTIVATED, REDEEMED, ZEROED, ARCHIVED
  balance               FLOAT          NOT NULL, -- ledger balance
  available_balance     FLOAT          NOT NULL, -- ledger balance - pre-authorized amount(s)
  currency              VARCHAR(3)     NOT NULL, -- ISO 4217 currency codes
  last_activity         TIMESTAMP      NOT NULL,
  CONSTRAINT PK_card_acct PRIMARY KEY ( pan )
);
PARTITION TABLE card_account ON COLUMN pan;

CREATE TABLE card_activity(
  pan   		VARCHAR(16)    NOT NULL,
  date_time		TIMESTAMP      NOT NULL,
  activity_type         VARCHAR(8)     NOT NULL,
  cr_dr                 VARCHAR(1)     NOT NULL,
  amount                FLOAT          NOT NULL
);
PARTITION TABLE card_activity ON COLUMN pan;

CREATE INDEX IDX_card_activity_pan_date ON card_activity (pan, date_time);

CREATE PROCEDURE FROM CLASS procedures.Authorize;
PARTITION PROCEDURE Authorize ON TABLE card_activity COLUMN pan PARAMETER 0;

CREATE PROCEDURE FROM CLASS procedures.Redeem;
PARTITION PROCEDURE Redeem ON TABLE card_activity COLUMN pan PARAMETER 0;

CREATE PROCEDURE FROM CLASS procedures.Transfer;

