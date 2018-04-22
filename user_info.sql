use bc_server_db;
create table user_info
(
    id bigint unsigned not null auto_increment primary key,
	ctime timestamp default CURRENT_TIMESTAMP,
	mtime timestamp default CURRENT_TIMESTAMP on UPDATE CURRENT_TIMESTAMP,
    name varchar(128) not null default "" UNIQUE,
    e_mail varchar(256)  not null default "" UNIQUE,
    phone char(32) not null default "" UNIQUE,
	is_develop boolean not null default false,
    password char(64) not null
);
