use bc_server_db;
create table dev_info
(
	id bigint unsigned not null auto_increment primary key,
	ctime timestamp default CURRENT_TIMESTAMP,
	mtime timestamp default CURRENT_TIMESTAMP on UPDATE CURRENT_TIMESTAMP,
	/* Serial Number */
	sn char(64) not null unique,
	/* Administrator user ID */
	admin_id bigint unsigned not null,
	/* The password to login in BcServer */
    password char(64) not null,
	/* The ID of system_signal_table_info */
	sys_sig_tab_id int unsigned not null default 0,
	/* The ID of system_signal_custom_info */
	sys_sig_diff_tab_id int unsigned not null default 0, 
	/* The ID of custom_signal_table_info */
	cus_sig_tab_id int unsigned not null default 0,
	/* The ID of user_change_signal_attr_info */
	user_cus_sig_tab_id tinyint unsigned not null default 0,

);
