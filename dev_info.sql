use bc_server_db;
create table dev_info
(
	dev_uniq_id bigint unsigned not null auto_increment primary key,
	user_id int unsigned not null,
    dev_password char(32) not null,
	dev_id smallint unsigned not null,
	dev_name char(128) not null default "", 
	sys_sig_tab_id int unsigned not null default 0
);
