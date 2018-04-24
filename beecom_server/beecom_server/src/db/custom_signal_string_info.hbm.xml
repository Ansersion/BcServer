USE bc_server_db;

CREATE TABLE custom_signal_string_info
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	
	/* Customized signal name */
	/* ID in custom_signal_name_lang_info table */
	cus_sig_name_lang_id INT UNSIGNED NOT NULL,  
	
	permission TINYINT UNSIGNED NOT NULL DEFAULT 0,
	def_val VARCHAR(512) NOT NULL DEFAULT "",
	
	/* Group language resource is from the sys_group_language_resource.csv */
	/* 0: no group language ID; */
	/* 0xFFFFFFFF: search for custom signal group language ID*/
	group_lang_id SMALLINT UNSIGNED NOT NULL DEFAULT 0, 
	
	/* ID in custom_signal_group_lang_info table */
	/* Valid when "group_lang_id == 0xFFFFFFFF" */
	cus_group_lang_id INT UNSIGNED NOT NULL DEFAULT 0, 
	
	en_statistics BOOLEAN NOT NULL DEFAULT TRUE,
	custom_signal_id INT UNSIGNED NOT NULL, 
	
	UNIQUE INDEX i_custom_signal_id(custom_signal_id)
);