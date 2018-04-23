USE bc_server_db;

CREATE TABLE custom_signal_info
(
	id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	
	/* Whether a alarm signal */
	is_alarm BOOLEAN NOT NULL DEFAULT FALSE,
	
	/* Value type 0-6:u32,u16,i32,i16,enum,float,string */
	val_type TINYINT UNSIGNED NOT NULL,
	
	/* ID in signal_info table */
	signal_id BIGINT UNSIGNED NOT NULL, 
	
	UNIQUE INDEX i_signal_id(signal_id)
);