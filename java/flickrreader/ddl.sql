CREATE TABLE stream (
    image_id        VARCHAR(41)         NOT NULL,
    title           VARCHAR(1000)        NOT NULL,
    json            VARCHAR(10000)      NOT NULL,
    CONSTRAINT stream_id_key PRIMARY KEY 
    (image_id)
);

CREATE TABLE tags (
    image_id        VARCHAR(41)         NOT NULL,
    tag             VARCHAR(100)        NOT NULL
);

CREATE VIEW tag_stats (
    tag,
    tag_count )
    AS
    SELECT tag, COUNT(*) as tag_count
    FROM tags
    GROUP BY tag;

PARTITION TABLE stream ON COLUMN image_id;
PARTITION TABLE tags ON COLUMN image_id;

CREATE INDEX tag_idx ON tags(tag);

CREATE PROCEDURE FROM CLASS com.voltdb.procedures.UpsertFlickrEntry;
CREATE PROCEDURE GetTopTags AS select tag, sum(tag_count) as tag_sum from tag_stats group by tag order by tag_sum desc, tag limit 20;

PARTITION PROCEDURE UpsertFlickrEntry ON TABLE stream COLUMN image_id PARAMETER 0;