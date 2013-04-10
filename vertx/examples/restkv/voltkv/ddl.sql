CREATE TABLE store (
  key varchar(256) not null
, value varbinary(1048576) not null
, PRIMARY KEY (key)
);
PARTITION TABLE store on COLUMN key;

CREATE PROCEDURE FROM CLASS voltkv.procedures.Put;

CREATE PROCEDURE voltkv.procedures.Get AS 
  SELECT VALUE FROM store WHERE key = ?;
PARTITION PROCEDURE Get on TABLE store COLUMN key;

CREATE PROCEDURE voltkv.procedures.Remove AS DELETE FROM store WHERE key = ?;
PARTITION PROCEDURE Remove on TABLE store COLUMN key;
