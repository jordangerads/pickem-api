alter table users alter column password drop not null;
alter table users add column "is_active" boolean not null default false;
alter table users add column "registration_code" varchar(256);