USE bc_server_db;

CREATE TABLE custom_signal_name_lang_info
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	custom_signal_name INT UNSIGNED NOT NULL default 0,
	
	UNIQUE INDEX i_custom_signal_name(custom_signal_name)
);
