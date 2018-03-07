create table Teams (
  team_id BIGSERIAL PRIMARY KEY NOT NULL,
  abbreviation varchar(128) NOT NULL,
  city varchar(128) NOT NULL,
  team_name varchar(128) NOT NULL
);

alter table game add constraint fk_home_team_team foreign key (home_team_id) references Teams (team_id);
alter table game add constraint fk_away_team_team foreign key (away_team_id) references Teams (team_id);