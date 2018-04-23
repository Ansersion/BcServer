use bc_server_db;

create table system_signal_i16_info
(
	id bigint unsigned not null auto_increment primary key,
	ctime timestamp default CURRENT_TIMESTAMP,
	mtime timestamp default CURRENT_TIMESTAMP on UPDATE CURRENT_TIMESTAMP,
	
	/* The name can't be changed */
	/* sys_sig_name_lang_id bigint unsigned not null,   */
	
	permission tinyint unsigned not null default 0,
	min_val int not null default 0x8000,
	max_val int unsigned not null default 0x7FFF,
	def_val int unsigned not null default 0,
	
	/* Group language resource is from the sys_group_language_resource.csv */
	group_lang_id int unsigned not null default 0, 
	
	en_statistics boolean not null default true,
	
	/* ID in system_signal_info table */
	system_signal_id bigint not null, 
	
	UNIQUE INDEX i_system_signal_id(system_signal_id)
);