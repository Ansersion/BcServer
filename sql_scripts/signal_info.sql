USE bc_server_db;

CREATE TABLE signal_info
(
	id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	
	/* Signal ID 0x0000-0xFFFF */
	/* 0x0000-0xDFFF: customized signal; 0xE000-0xFFFF: system signal*/
	signal_id SMALLINT NOT NULL,
	
	/* ID in dev_info table */
	dev_id BIGINT UNSIGNED NOT NULL, 
	
	UNIQUE INDEX i_dev_id(dev_id)
);