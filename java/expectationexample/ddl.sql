CREATE TABLE user_table (
    user_name        varchar(200)        UNIQUE NOT NULL,
    password         varchar(100)        NOT NULL,
    CONSTRAINT user_name_idx PRIMARY KEY
    (user_name)
);
PARTITION TABLE user_table ON COLUMN user_name;

CREATE INDEX user_password_idx on user_table (user_name, password);

CREATE PROCEDURE FROM CLASS com.voltdb.expectation.procs.LoginProc1;
CREATE PROCEDURE FROM CLASS com.voltdb.expectation.procs.LoginProc2;