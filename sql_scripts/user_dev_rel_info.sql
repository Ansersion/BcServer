USE bc_server_db;

CREATE TABLE user_dev_rel_info
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	
	/* The ID of user_info  */
	user_id INT UNSIGNED NOT NULL, 
	/* The ID of sn_info  */
	sn_id INT UNSIGNED NOT NULL,
	/* The auth of the relationship  */
	auth TINYINT UNSIGNED NOT NULL DEFAULT 6,
	/* To indicate whether the user has got the signal table */
	unconfig_flag BOOLEAN NOT NULL DEFAULT TRUE, 
	
	INDEX i_user_id(user_id),
	INDEX i_sn_id(sn_id)
	
	
);
