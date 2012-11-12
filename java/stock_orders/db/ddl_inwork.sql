CREATE TABLE firms (
  id              INTEGER          UNIQUE NOT NULL,
  member_name     VARCHAR(100)     NOT NULL,
  market_name	  VARCHAR(10)      NOT NULL,
  address	  VARCHAR(100),
  phone		  VARCHAR(20),
  fax		  VARCHAR(20),
  website	  VARCHAR(50),
  email		  VARCHAR(50),
  CONSTRAINT IDX_firms_PK PRIMARY KEY (id)
);

-- market tick data trades
CREATE TABLE trades (
  symbol	 VARCHAR(32)	   NOT NULL,
  datetime       TIMESTAMP         NOT NULL,
  price		 FLOAT		   NOT NULL,
  volume	 INTEGER	   NOT NULL,
  exchange	 VARCHAR(2)	   NOT NULL,
  sales_cond	 VARCHAR(4),
  correction     VARCHAR(2),
  seq_no	 BIGINT,
  trade_stop	 VARCHAR(1),
  --trade_source	 VARCHAR(1),
  --mds_127_trf	 VARCHAR(3),
  --trf		 VARCHAR(1),
  --exclude	 VARCHAR(1),
  --filt_price	 FLOAT,
  uptick	 TINYINT,
  downtick	 TINYINT,
  sametick	 TINYINT
);
-- partition: symbol

CREATE TABLE last_prices (
  symbol	 VARCHAR(32)	   NOT NULL,
  datetime       TIMESTAMP         NOT NULL,
  price		 FLOAT		   NOT NULL,
  CONSTRAINT IDX_last_price_PK PRIMARY KEY (symbol)
);
-- partition: symbol

CREATE TABLE acct_trades (
  id              BIGINT           NOT NULL,
  acct_id         INTEGER         NOT NULL,
  acct_type	  VARCHAR(1),
  symbol	  VARCHAR(32)     NOT NULL,
  buy_sell	  VARCHAR(2)      NOT NULL, -- B,SL,...
  buy_shares	  INTEGER,
  sell_shares	  INTEGER,
  price	  	  FLOAT,
  traded	  TIMESTAMP
);
-- partition: acct_id

CREATE TABLE accounts (
  acct_id            INTEGER       NOT NULL,
  acct_name          VARCHAR(100)  NOT NULL,
  cash_bal           FLOAT         NOT NULL,
  open_date          INTEGER,
  CONSTRAINT IDX_accounts_PK PRIMARY KEY (acct_id)  
);
-- partition: acct_id

CREATE TABLE acct_holdings (
  acct_id            INTEGER       NOT NULL,
  symbol	     VARCHAR(32)   NOT NULL,
  shares	     INTEGER	   NOT NULL,
  CONSTRAINT IDX_acct_holdings_PK PRIMARY KEY (acct_id,symbol)  
);
-- partition: acct_id

