create table admins (
    id bigserial not null,
    login_id varchar(255) not null,
    password varchar(255) not null,
    name varchar(255),
    role varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp,
    deleted_at timestamp,
    primary key (id),
    unique (login_id)
);