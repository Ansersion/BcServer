use bc_server_db;

create table custom_signal_common_info
(
	id bigint unsigned not null auto_increment primary key,
	ctime timestamp default CURRENT_TIMESTAMP,
	mtime timestamp default CURRENT_TIMESTAMP on UPDATE CURRENT_TIMESTAMP,
	cus_sig_name char(64) not null default "",
);