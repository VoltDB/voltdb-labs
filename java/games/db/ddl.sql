CREATE TABLE games(
  game_id               INTEGER        NOT NULL,
  title                 VARCHAR(50)    NOT NULL,
  CONSTRAINT PK_games PRIMARY KEY (game_id)
);
-- REPLICATED

CREATE TABLE players(
  player_id             INTEGER         NOT NULL,
  full_name             VARCHAR(50)     NOT NULL,
  email                 VARCHAR(100),
  CONSTRAINT PK_players PRIMARY KEY (player_id)
);
-- PARTITION by player_id

CREATE TABLE game_players(
  game_id               INTEGER        NOT NULL,
  player_id             INTEGER        NOT NULL,
  credits               INTEGER        NOT NULL,
  current_level         INTEGER        NOT NULL,
  score                 INTEGER        NOT NULL,
  active_session        INTEGER        NOT NULL,
  --player_name           VARCHAR(20)    NOT NULL,
  CONSTRAINT PK_game_players PRIMARY KEY (game_id, player_id)
);
-- PARTITION by player_id
CREATE INDEX idx_game_players_game_scores ON game_players (game_id, score, player_id);
--CREATE INDEX idx_game_players_scores ON game_players (score);

--CREATE TABLE game_leaders(
--  game_id               INTEGER        NOT NULL,
--  player_id             INTEGER        NOT NULL,
--  score                 INTEGER        NOT NULL,
--  CONSTRAINT PK_game_leaders PRIMARY KEY (game_id, score, player_id)
--);

CREATE VIEW game_stats (
  game_id,
  players,
  active_sessions
) AS
SELECT
  game_id,
  COUNT(*),
  SUM(active_session)
FROM game_players
GROUP BY game_id;
