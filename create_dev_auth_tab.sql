use bc_server_db;
create table dev_auth
(
	dev_uniq_id bigint unsigned not null primary key,
	admin_user int unsigned not null,
	admin_auth tinyint unsigned not null default 7,
	user_id1 int unsigned null default 0, 
	user_id1_auth tinyint unsigned null default 0, 
	user_id2 int unsigned null default 0, 
	user_id2_auth tinyint unsigned null default 0, 
	user_id3 int unsigned null default 0, 
	user_id3_auth tinyint unsigned null default 0, 
	user_id4 int unsigned null default 0,
	user_id4_auth tinyint unsigned null default 0
);

