create table Users (
  user_id BIGSERIAL PRIMARY KEY NOT NULL,
  first_name varchar(128) NOT NULL,
  last_name varchar(128) NOT NULL
);

create table Game (
  game_id BIGSERIAL PRIMARY KEY NOT NULL,
  season INTEGER NOT NULL,
  week INTEGER NOT NULL,
  home_team_id BIGINT NOT NULL,
  away_team_id BIGINT NOT NULL,
  game_complete BOOLEAN NOT NULL DEFAULT FALSE,
  winning_team_id BIGINT
);

create table Pool (
  pool_id BIGSERIAL PRIMARY KEY NOT NULL,
  pool_name varchar(256) NOT NULL,
  scoring_method INTEGER NOT NULL
);

create table picks (
  pick_id BIGSERIAL PRIMARY KEY NOT NULL,
  user_id int NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
  pool_id int NOT NULL REFERENCES pool (pool_id) ON DELETE CASCADE,
  game_id int NOT NULL REFERENCES game (game_id) ON DELETE CASCADE,
  chosen_team_id BIGINT,
  confidence INTEGER
);

create table user_pool (
  user_pool_id BIGSERIAL PRIMARY KEY NOT NULL,
  user_id int NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
  pool_id int NOT NULL REFERENCES pool (pool_id) ON DELETE CASCADE,
  user_role varchar(32) NOT NULL
);