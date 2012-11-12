-- metro cards
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
-- partition by: acct_num

-- card activity
CREATE TABLE card_activity(
  pan   		VARCHAR(16)    NOT NULL,
  date_time		TIMESTAMP      NOT NULL,
  activity_type         VARCHAR(8)     NOT NULL,
  cr_dr                 VARCHAR(1)     NOT NULL,
  amount                FLOAT          NOT NULL
);
-- partition by acct_num
CREATE INDEX IDX_card_activity_pan_date ON card_activity (pan, date_time);
