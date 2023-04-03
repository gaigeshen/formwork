alter table system_role_user drop index ix_system_role_user_role_id_user_id;
alter table system_role_user add unique index ix_system_role_user_role_id_user_id(role_id, user_id);