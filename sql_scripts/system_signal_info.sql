USE bc_server_db;

CREATE TABLE system_signal_info
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	
	/* if(config_def) no custom info 
	else search for customized info */
	config_def BOOLEAN NOT NULL DEFAULT TRUE,
	
	/* ID in signal_info table */
	signal_id INT UNSIGNED NOT NULL, 
	
	UNIQUE INDEX i_signal_id(signal_id)
);