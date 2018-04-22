use bc_server_db;

create table dev_info
(
	id bigint unsigned not null auto_increment primary key,
	ctime timestamp default CURRENT_TIMESTAMP,
	mtime timestamp default CURRENT_TIMESTAMP on UPDATE CURRENT_TIMESTAMP,
	/* The ID of user_info  */
	user_id bigint not null, 
	/* The ID of dev_info  */
	dev_id bigint not null,
	/* The auth of the relationship  */
	auth tinyint unsigned not null DEFAULT '6',
);