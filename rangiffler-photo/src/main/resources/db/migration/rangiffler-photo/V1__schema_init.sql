create table if not exists `photo`
(
    id                      binary(16) unique not null default (UUID_TO_BIN(UUID(), true)),
    user_id                 binary(16)        not null,
    country                 binary(16)        not null,
    `description`           varchar(255),
    photo                   longblob,
    created_date            datetime          not null,
    primary key (id)
);

create table if not exists `like`
(
    id                      binary(16) unique not null default (UUID_TO_BIN(UUID(), true)),
    user_id                 binary(16)        not null,
    created_date            datetime          not null,
    primary key (id)
);

create table if not exists `photo_like`
(
    photo_id                 binary(16)        not null,
    like_id                  binary(16)        not null,
    user_id                  binary(16)        not null,
    primary key (photo_id, like_id, user_id),
    constraint ph_like_photo_id foreign key (photo_id) references `photo` (id),
    constraint lk_like_photo_id foreign key (like_id) references `like` (id)
);