USE bc_server_db;

CREATE TABLE custom_signal_info
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	
	/* Whether a alarm signal */
	is_alarm BOOLEAN NOT NULL DEFAULT FALSE,
	
	/* Value type 0-6:u32,u16,i32,i16,enum,float,string */
	val_type TINYINT UNSIGNED NOT NULL,
	
	/* ID in signal_info table */
	signal_id INT UNSIGNED NOT NULL, 

	/* Customized signal name */
	/* ID in custom_signal_name_lang_info table */
	cus_sig_name_lang_id INT UNSIGNED NOT NULL DEFAULT 0,  
	
	/* Customized signal unit */
	/* ID in custom_unit_lang_info table */
	cus_sig_unit_lang_id INT UNSIGNED NOT NULL DEFAULT 0,  

	/* Group language resource is from the sys_group_language_resource.csv */
	/* 0: no group language ID; */
	/* 0xFFFFFFFF: search for custom signal group language ID*/
	group_lang_id SMALLINT UNSIGNED NOT NULL DEFAULT 0, 
	
	/* ID in custom_signal_group_lang_info table */
	/* Valid when "group_lang_id == 0xFFFFFFFF" */
	cus_group_lang_id INT UNSIGNED NOT NULL DEFAULT 0, 
	
	UNIQUE INDEX i_signal_id(signal_id)
);
