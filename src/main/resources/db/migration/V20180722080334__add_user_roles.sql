create table user_role (
  user_role_id BIGSERIAL PRIMARY KEY NOT NULL,
  user_id int NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
  role varchar(64) NOT NULL
);

alter table user_role add constraint user_role_unique unique (user_id, role);