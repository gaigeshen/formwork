create table system_user (
    id bigint auto_increment primary key,
    user_name varchar(32) null,
    password varchar(32) null,
    disabled bit null,
    create_time datetime null,
    update_time datetime null,
    create_user_id bigint null,
    update_user_id bigint null,
    version int null
);

create table system_role (
     id bigint auto_increment primary key,
     name varchar(32) null,
     create_time datetime null,
     update_time datetime null,
     create_user_id bigint null,
     update_user_id bigint null,
     version int null
);

create table system_role_user (
    id bigint auto_increment primary key,
    role_id bigint null,
    user_id bigint null,
    create_time datetime null,
    update_time datetime null,
    create_user_id bigint null,
    update_user_id bigint null,
    version int null
);

alter table system_user add unique index uk_system_user_user_name(user_name);
alter table system_role add unique index uk_system_role_name(name);
alter table system_role_user add index ix_system_role_user_role_id_user_id(role_id, user_id);
