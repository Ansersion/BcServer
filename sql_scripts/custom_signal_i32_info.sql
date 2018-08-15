USE bc_server_db;

CREATE TABLE custom_signal_i32_info
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	
	permission TINYINT UNSIGNED NOT NULL DEFAULT 0,
	min_val INT NOT NULL DEFAULT 2147483647, /* 0x7FFFFFFF */
	max_val INT NOT NULL DEFAULT 2147483647, /* 0x7FFFFFFF */
	def_val INT NOT NULL DEFAULT 0,
	
	en_statistics BOOLEAN NOT NULL DEFAULT TRUE,
	custom_signal_id INT UNSIGNED NOT NULL, 
	
	UNIQUE INDEX i_custom_signal_id(custom_signal_id)
);
