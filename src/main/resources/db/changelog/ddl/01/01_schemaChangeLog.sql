--changeset nutkin:1

create schema auth;

create sequence auth.users_id_seq;

create table auth.users
(
    id         int8 primary key default nextval('auth.users_id_seq'),
    username   varchar(50) unique not null,
    first_name varchar(50)        not null,
    last_name  varchar(50)        not null,
    password   varchar(255)       not null,
    version    int8             default 0
);

create index meetings_user_id_index on auth.users (username);

-- roles
create sequence auth.roles_id_seq;

create table auth.roles
(
    id       int8 primary key default nextval('auth.roles_id_seq'),
    role_key varchar(50) unique not null
);

create table auth.user_roles
(
    user_id int8 references auth.users (id),
    role_id int8 references auth.roles (id),
    primary key (user_id, role_id)
);

create index meeting_members_reverse_index on auth.user_roles (user_id, role_id);