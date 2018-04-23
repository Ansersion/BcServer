USE bc_server_db;
CREATE TABLE dev_info
(
	id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	/* Serial Number */
	sn CHAR(64) NOT NULL UNIQUE,
	/* Administrator user ID */
	admin_id BIGINT UNSIGNED NOT NULL,
	/* The password to login in BcServer */
    password CHAR(64) NOT NULL,

	INDEX i_admin_id(admin_id),
	INDEX i_sn(sn)
);
