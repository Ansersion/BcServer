use bc_server_db;

create table dev_info
(
	id bigint unsigned not null auto_increment primary key,
	ctime timestamp default CURRENT_TIMESTAMP,
	mtime timestamp default CURRENT_TIMESTAMP on UPDATE CURRENT_TIMESTAMP,
	sn char(64) not null unique,
	develop_user_id bigint unsigned not null,
	
	activite_date date unsigned not null default 0,
	expired_date date unsigned not null default 0,
	
	UNIQUE INDEX i_sn(sn), 
	INDEX i_sn(develop_user_id)
);