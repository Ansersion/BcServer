USE bc_server_db;

CREATE TABLE custom_signal_string_info
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	
	permission TINYINT UNSIGNED NOT NULL DEFAULT 0,

    /* id in custom_signal_string_default_value_entity_info.sql */
	def_val INT UNSIGNED NOT NULL DEFAULT 0,
	
	en_statistics BOOLEAN NOT NULL DEFAULT TRUE,
	custom_signal_id INT UNSIGNED NOT NULL, 
	
	UNIQUE INDEX i_custom_signal_id(custom_signal_id)
);
