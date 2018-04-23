USE bc_server_db;

CREATE TABLE custom_signal_float_info
(
	id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	
	/* Customized signal name */
	/* ID in custom_signal_name_lang_info table */
	cus_sig_name_lang_id BIGINT UNSIGNED NOT NULL,  
	
	/* Customized signal unit */
	/* ID in custom_unit_lang_info table */
	cus_sig_unit_lang_id BIGINT UNSIGNED NOT NULL,  
	
	permission TINYINT UNSIGNED NOT NULL DEFAULT 0,
	accuracy TINYINT UNSIGNED NOT NULL DEFAULT 0,
	min_val FLOAT NOT NULL DEFAULT 0x7FFFFFFF,
	max_val FLOAT NOT NULL DEFAULT 0x7FFFFFFF,
	def_val FLOAT NOT NULL DEFAULT 0,
	
	/* Group language resource is from the sys_group_language_resource.csv */
	/* 0: no group language ID; */
	/* 0xFFFFFFFF: search for custom signal group language ID*/
	group_lang_id INT UNSIGNED NOT NULL DEFAULT 0, 
	
	/* ID in custom_signal_group_lang_info table */
	/* Valid when "group_lang_id == 0xFFFFFFFF" */
	cus_group_lang_id BIGINT UNSIGNED NOT NULL DEFAULT 0, 
	
	en_statistics BOOLEAN NOT NULL DEFAULT TRUE,
	custom_signal_id BIGINT UNSIGNED NOT NULL, 
	
	UNIQUE INDEX i_custom_signal_id(custom_signal_id)
);