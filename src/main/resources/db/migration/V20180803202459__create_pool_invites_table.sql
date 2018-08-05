create table pool_invites (
  pool_invite_id BIGSERIAL PRIMARY KEY NOT NULL,
  inviting_user_id int NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
  pool_id int NOT NULL REFERENCES pool (pool_id) ON DELETE CASCADE,
  invitee_email varchar(256) NOT NULL,
  invite_status varchar(64) NOT NULL DEFAULT 'PENDING'
);

alter table pool_invites add constraint pool_id_email_unique unique (pool_id, invitee_email);