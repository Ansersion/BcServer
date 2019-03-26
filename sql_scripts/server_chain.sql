USE bc_server_db;
CREATE TABLE server_chain
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	/* Serial Number */
	client_id INT UNSIGNED NOT NULL,
	/* The upper server */
    upper_server VARCHAR(128) DEFAULT '',
	/* The upper server type */
    upper_server_type TINYINT DEFAULT 0,
	/* The lower server */
    lower_server VARCHAR(128) DEFAULT '',
	/* The lower server type */
    lower_server_type TINYINT DEFAULT 0,

	UNIQUE INDEX i_dev_id(client_id)
);
