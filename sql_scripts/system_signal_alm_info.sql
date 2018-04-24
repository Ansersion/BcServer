USE bc_server_db;

CREATE TABLE system_signal_alm_info
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	alm_class TINYINT NOT NULL DEFAULT 4,
	dly_before_alm TINYINT UNSIGNED NOT NULL DEFAULT 10,
	dly_after_alm TINYINT UNSIGNED NOT NULL DEFAULT 10,
	
	/* ID in system_signal_info table */
	system_signal_id INT UNSIGNED NOT NULL, 
	
	UNIQUE INDEX i_system_signal_id(system_signal_id)
);