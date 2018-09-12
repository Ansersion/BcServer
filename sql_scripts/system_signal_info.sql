USE bc_server_db;

CREATE TABLE system_signal_info
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	
	/* if(custom_flags == 0) no custom info 
	else search for customized info */
	custom_flags SMALLINT UNSIGNED NOT NULL DEFAULT 0,
	
	/* ID in signal_info table */
	signal_id INT UNSIGNED NOT NULL, 
	
	UNIQUE INDEX i_signal_id(signal_id)
);
