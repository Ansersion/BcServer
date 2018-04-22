use bc_server_db;

create table custom_signal_u16_info
(
	id bigint unsigned not null auto_increment primary key,
	ctime timestamp default CURRENT_TIMESTAMP,
	mtime timestamp default CURRENT_TIMESTAMP on UPDATE CURRENT_TIMESTAMP,
	cus_sig_name_lang_id bigint unsigned not null,  
	permission tinyint unsigned not null default 0,
	accuracy tinyint unsigned not null default 0,
	min_val int unsigned not null default 0,
	max_val int unsigned not null default 0xFFFFFFFE,
	def_val int unsigned not null default 0,
	group_lang_id bigint unsigned not null default 0, 
	en_statistics boolean not null default true,
	custom_signal_id bigint not null unique, 
);