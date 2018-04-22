use bc_server_db;

create table custom_signal_attr_info
(
	id bigint unsigned not null auto_increment primary key,
	ctime timestamp default CURRENT_TIMESTAMP,
	mtime timestamp default CURRENT_TIMESTAMP on UPDATE CURRENT_TIMESTAMP,
	notifying boolean not null default false,
	custom_signal_id bigint not null unique
);