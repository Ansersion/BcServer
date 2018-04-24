USE bc_server_db;

CREATE TABLE custom_signal_alm_info
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	
	/* Customized signal name */
	/* ID in custom_signal_name_lang_info table */
	cus_sig_name_lang_id INT UNSIGNED NOT NULL,  
	
	alm_class TINYINT NOT NULL DEFAULT 4,
	dly_before_alm TINYINT UNSIGNED NOT NULL DEFAULT 10,
	dly_after_alm TINYINT UNSIGNED NOT NULL DEFAULT 10,
	
	custom_signal_id INT UNSIGNED NOT NULL, 
	
	UNIQUE INDEX i_custom_signal_id(custom_signal_id)
);