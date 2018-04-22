use bc_server_db;

create table signal_info
(
	id bigint unsigned not null auto_increment primary key,
	ctime timestamp default CURRENT_TIMESTAMP,
	mtime timestamp default CURRENT_TIMESTAMP on UPDATE CURRENT_TIMESTAMP,
	signal_id smallint not null,
	dev_id bigint unsigned not null, 
);