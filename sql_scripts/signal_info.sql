USE bc_server_db;

CREATE TABLE signal_info
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	
	/* Signal ID 0x0000-0xFFFF */
	/* 0x0000-0xDFFF: customized signal; 0xE000-0xFFFF: system signal*/
	signal_id SMALLINT UNSIGNED NOT NULL,
	
	/* ID in dev_info table */
	dev_id INT UNSIGNED NOT NULL, 
	
	/* Attributes which users can change*/
	notifying BOOLEAN NOT NULL DEFAULT false,
	
	/* Attributes which users can change*/
	display BOOLEAN NOT NULL DEFAULT true,

	/* Alarm class: 0-4(0-emergency, 1-serious, 2-warning, 3-attention, 4-note), 127 means not alarm, default 127*/
	alm_class TINYINT UNSIGNED NOT NULL DEFAULT 127,

	/* delay before alarm, default 5 seconds */
	alm_dly_bef TINYINT UNSIGNED NOT NULL DEFAULT 5,

	/* delay after alarm, default 5 seconds */
	alm_dly_aft TINYINT UNSIGNED NOT NULL DEFAULT 5,
	
	INDEX i_dev_id(dev_id)
);
