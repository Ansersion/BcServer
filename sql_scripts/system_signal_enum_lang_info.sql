USE bc_server_db;

CREATE TABLE system_signal_enum_lang_info
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	enum_key SMALLINT UNSIGNED NOT NULL,  
	enum_val VARCHAR(128) NOT NULL DEFAULT "",
	
	/* ID in system_signal_enum_info table */
	sys_sig_enm_id INT UNSIGNED NOT NULL,
	
	UNIQUE INDEX i_sys_sig_enm_id(sys_sig_enm_id)
);