CREATE TABLE symbols (
  symbol	   VARCHAR(32)	    UNIQUE NOT NULL,
  company_name	   VARCHAR(100),
  last_sale	   FLOAT,
  market_cap	   FLOAT,
  total_shares	   INTEGER,
  ipo_year	   SMALLINT,
  sector	   VARCHAR(100),
  industry	   VARCHAR(100),
  CONSTRAINT IDX_symbols_PK PRIMARY KEY (symbol)
);
-- partition: symbol

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
-- partition: acct_id

CREATE INDEX IDX_orders_acct_symbol ON orders (acct_id, symbol);

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

