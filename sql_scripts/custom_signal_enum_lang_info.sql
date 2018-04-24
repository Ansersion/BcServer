USE bc_server_db;

CREATE TABLE custom_signal_enum_lang_info
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	enum_key SMALLINT UNSIGNED NOT NULL,  
	enum_val VARCHAR(128) NOT NULL, 
	cus_sig_enm_id INT UNSIGNED NOT NULL,
	
	INDEX i_cus_sig_enm_id(cus_sig_enm_id)
);