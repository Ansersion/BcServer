USE bc_server_db;

CREATE TABLE system_signal_string_info
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	
	/* The name can't be changed */
	/* sys_sig_name_lang_id INT unsigned not null,   */
	
	permission TINYINT UNSIGNED NOT NULL DEFAULT 4,
	def_val VARCHAR(512) NOT NULL DEFAULT "",
	
	/* Group language resource is from the sys_group_language_resource.csv */
	group_lang_id SMALLINT UNSIGNED NOT NULL DEFAULT 0, 
	
	en_statistics BOOLEAN NOT NULL DEFAULT TRUE,
	
	/* ID in system_signal_info table */
	system_signal_id INT UNSIGNED NOT NULL, 
	
	UNIQUE INDEX i_system_signal_id(system_signal_id)
);
