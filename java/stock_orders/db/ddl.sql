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

CREATE TABLE symbols (
  symbol	   VARCHAR(32)	    NOT NULL,
  company_name	   VARCHAR(100),
  last_sale	   FLOAT,
  market_cap	   FLOAT,
  total_shares	   INTEGER,
  ipo_year	   SMALLINT,
  sector	   VARCHAR(100),
  industry	   VARCHAR(100),
  CONSTRAINT IDX_symbols_PK PRIMARY KEY (symbol)
);
PARTITION TABLE symbols ON COLUMN symbol;

-- internal orders
CREATE TABLE orders (
  id              BIGINT          NOT NULL,
  version	  SMALLINT        NOT NULL,
  acct_id         INTEGER         NOT NULL,
  acct_type	  VARCHAR(1),
  symbol	  VARCHAR(32)     NOT NULL,
  b_s   	  VARCHAR(2)      NOT NULL, -- B,S
  --b_shares	  INTEGER,
  --b_pend_shares   INTEGER,
  --b_canc_shares   INTEGER,
  --s_shares	  INTEGER,
  --s_pend_shares   INTEGER,
  --s_canc_shares	  INTEGER,
  b_shares	  DECIMAL,
  b_pend_shares   DECIMAL,
  b_canc_shares   DECIMAL,
  s_shares	  DECIMAL,
  s_pend_shares   DECIMAL,
  s_canc_shares	  DECIMAL,
  stop_price	  FLOAT,
  limit_price	  FLOAT,
  expires	  TIMESTAMP,
  time_in_force	  VARCHAR(3),
  handling_code   VARCHAR(20),
  originated	  TIMESTAMP,
  modified	  TIMESTAMP,
  CONSTRAINT IDX_orders_PK PRIMARY KEY (id)
);
PARTITION TABLE orders ON COLUMN acct_id;
-- partition: acct_id

CREATE INDEX IDX_orders_acct_symbol ON orders (acct_id, symbol);

------------------------- VIEWS ----------------------------

CREATE VIEW symbol_vol (
  symbol,
  orders,
  buy_vol,
  sell_vol,
  buy_pend_vol,
  sell_pend_vol,
  buy_canc_vol,
  sell_canc_vol
) AS
SELECT
  symbol,
  COUNT(*),
  SUM(b_shares),
  SUM(s_shares),
  SUM(b_pend_shares),
  SUM(s_pend_shares),
  SUM(b_canc_shares),
  SUM(s_canc_shares)
FROM orders
GROUP BY symbol;

CREATE VIEW acct_vol (
  acct_id,
  orders,
  buy_vol,
  sell_vol,
  buy_pend_vol,
  sell_pend_vol,
  buy_canc_vol,
  sell_canc_vol
) AS
SELECT
  acct_id,
  COUNT(*),
  SUM(b_shares),
  SUM(s_shares),
  SUM(b_pend_shares),
  SUM(s_pend_shares),
  SUM(b_canc_shares),
  SUM(s_canc_shares)
FROM orders
GROUP BY acct_id;

CREATE VIEW symbol_acct_vol (
  symbol,
  acct_id,
  orders,
  buy_vol,
  sell_vol,
  buy_pend_vol,
  sell_pend_vol,
  buy_canc_vol,
  sell_canc_vol
) AS
SELECT
  symbol,
  acct_id,
  COUNT(*),
  SUM(b_shares),
  SUM(s_shares),
  SUM(b_pend_shares),
  SUM(s_pend_shares),
  SUM(b_canc_shares),
  SUM(s_canc_shares)
FROM orders
GROUP BY symbol, acct_id;

------------- STORED PROCEDURES --------------

CREATE PROCEDURE FROM CLASS procedures.UpsertSymbol;
PARTITION PROCEDURE UpsertSymbol ON TABLE symbols COLUMN symbol PARAMETER 0;

--CREATE PROCEDURE FROM CLASS procedures.InsertTrade;
--PARTITION PROCEDURE InsertTrade ON TABLE trades COLUMN symbol PARAMETER 0;

CREATE PROCEDURE SYMBOLS_selectall AS 
SELECT * FROM symbols ORDER BY symbol;

CREATE PROCEDURE select_max_order_id AS 
SELECT MAX(id) FROM orders;

CREATE PROCEDURE Top10BuyStocks AS 
SELECT * FROM symbol_vol ORDER BY buy_vol desc LIMIT 10;

CREATE PROCEDURE Top10VolStocks AS 
SELECT symbol, buy_vol + sell_vol as volume, orders, buy_vol, sell_vol, 
buy_pend_vol, sell_pend_vol, buy_canc_vol, sell_canc_vol 
FROM symbol_vol ORDER BY volume desc LIMIT 10;

CREATE PROCEDURE Top10VolAccts AS 
SELECT acct_id, buy_vol + sell_vol as volume, orders, buy_vol, sell_vol, 
buy_pend_vol, sell_pend_vol, buy_canc_vol, sell_canc_vol 
FROM acct_vol ORDER BY volume desc LIMIT 10;

CREATE PROCEDURE Top10VolAcctsForStock AS 
SELECT symbol, acct_id, buy_vol + sell_vol as volume, orders, buy_vol, sell_vol, 
buy_pend_vol, sell_pend_vol, buy_canc_vol, sell_canc_vol 
FROM symbol_acct_vol WHERE symbol = ? ORDER BY volume desc LIMIT 10;
PARTITION PROCEDURE Top10VolAcctsForStock ON TABLE SYMBOL_ACCT_VOL COLUMN symbol;

CREATE PROCEDURE Top10VolStocksForAcct AS 
SELECT acct_id, symbol, buy_vol + sell_vol as volume, orders, buy_vol, sell_vol, 
buy_pend_vol, sell_pend_vol, buy_canc_vol, sell_canc_vol 
FROM symbol_acct_vol WHERE acct_id = ? ORDER BY volume desc LIMIT 10;

CREATE PROCEDURE OrdersByAcctStock AS 
SELECT * FROM orders WHERE acct_id = ? AND symbol = ?;
PARTITION PROCEDURE OrdersByAcctStock ON TABLE orders COLUMN acct_id;

CREATE PROCEDURE OrdersByAcct AS 
SELECT * FROM orders WHERE acct_id = ?;
PARTITION PROCEDURE OrdersByAcct ON TABLE orders COLUMN acct_id;

CREATE PROCEDURE AcctVol AS 
SELECT acct_id, buy_vol + sell_vol as volume, orders, buy_vol, sell_vol, 
buy_pend_vol, sell_pend_vol, buy_canc_vol, sell_canc_vol 
FROM acct_vol WHERE acct_id = ?;
PARTITION PROCEDURE AcctVol ON TABLE orders COLUMN acct_id;
