USE bc_server_db;
CREATE TABLE dev_info
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	/* Serial Number */
	sn_id INT UNSIGNED NOT NULL,
	/* Administrator user ID */
	admin_id INT UNSIGNED NOT NULL,
	/* The password to login in BcServer */
    password CHAR(64) NOT NULL,
	/* A device can't change its signal table unless 'daily_sig_tab_change_times > 0*/
	daily_sig_tab_change_times TINYINT UNSIGNED NOT NULL DEFAULT 3,

	INDEX i_admin_id(admin_id),
	UNIQUE INDEX i_sn_id(sn_id)
);
