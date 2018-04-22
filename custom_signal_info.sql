use bc_server_db;

create table custom_signal_info
(
	id bigint unsigned not null auto_increment primary key,
	ctime timestamp default CURRENT_TIMESTAMP,
	mtime timestamp default CURRENT_TIMESTAMP on UPDATE CURRENT_TIMESTAMP,
	is_alarm boolean not null default false,
	signal_id bigint not null unique, 
);