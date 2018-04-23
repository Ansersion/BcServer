use bc_server_db;

create table system_signal_alm_info
(
	id bigint unsigned not null auto_increment primary key,
	ctime timestamp default CURRENT_TIMESTAMP,
	mtime timestamp default CURRENT_TIMESTAMP on UPDATE CURRENT_TIMESTAMP,
	alm_class tinyint not null default 4,
	dly_before_alm tinyint not null default 10,
	dly_after_alm tinyint not null default 10,
	
	/* ID in system_signal_info table */
	system_signal_id bigint not null, 
	
	UNIQUE INDEX i_system_signal_id(system_signal_id)
);