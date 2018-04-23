use bc_server_db;

create table custom_signal_u32_info
(
	id bigint unsigned not null auto_increment primary key,
	ctime timestamp default CURRENT_TIMESTAMP,
	mtime timestamp default CURRENT_TIMESTAMP on UPDATE CURRENT_TIMESTAMP,
	
	/* Customized signal name */
	/* ID in custom_signal_name_lang_info table */
	cus_sig_name_lang_id bigint unsigned not null,  
	
	
	permission tinyint unsigned not null default 0,
	min_val int unsigned not null default 0,
	max_val int unsigned not null default 0xFFFFFFFE,
	def_val int unsigned not null default 0,
	
	/* Group language resource is from the sys_group_language_resource.csv */
	group_lang_id int unsigned not null default 0, 
	
	/* ID in custom_signal_group_lang_info table */
	/* Valid when "group_lang_id == 0" */
	cus_group_lang_id bigint unsigned not null default 0, 

	en_statistics boolean not null default true,
	custom_signal_id bigint not null,  
	
	UNIQUE INDEX i_custom_signal_id(custom_signal_id)
);