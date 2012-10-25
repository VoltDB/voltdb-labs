CREATE TABLE tweet
(
  expires       bigint       NOT NULL
, lat           smallint     NOT NULL
, lon           smallint     NOT NULL
, tag           varchar(100) NOT NULL
);

CREATE TABLE tweetStats
(
  intervalID          BIGINT       NOT NULL
, tag            varchar(100) NOT NULL
, record_count        BIGINT       NOT NULL
);

CREATE INDEX IX_tweet_lat_lon_expires_tree
    ON tweet
    (
      lat
    , lon
    , expires
    )
;

CREATE VIEW v_tweet
(
  lat
, lon
, record_count
)
AS
   SELECT lat
        , lon
        , COUNT(*)
     FROM tweet
 GROUP BY lat
        , lon
;

CREATE VIEW v_tag
(
 lat
, lon
, tag
, record_count

)
AS
   SELECT lat
        , lon
        , tag
        , COUNT(*)
     FROM tweet
 GROUP BY lat
        , lon
        , tag
;

