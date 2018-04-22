use bc_server_db;

create table custom_signal_enum_lang_info
(
	id bigint unsigned not null auto_increment primary key,
	ctime timestamp default CURRENT_TIMESTAMP,
	mtime timestamp default CURRENT_TIMESTAMP on UPDATE CURRENT_TIMESTAMP,
	enum_key smallint unsigned not null,  
	enum_val varchar(128) not null default ""
	cus_sig_enm_id bigint not null,
);