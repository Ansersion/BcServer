use bc_server_db;
create table user_dev_relation
(
	user_id int unsigned not null,
	dev_uniq_id bigint unsigned not null,
	dev_id smallint unsigned not null,
	sys_sig_tab_id int unsigned not null default 0
);
