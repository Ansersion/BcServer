USE bc_server_db;

CREATE TABLE user_dev_rel_info
(
	id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	
	/* The ID of user_info  */
	user_id BIGINT UNSIGNED NOT NULL, 
	/* The ID of dev_info  */
	dev_id BIGINT UNSIGNED NOT NULL,
	/* The auth of the relationship  */
	auth TINYINT UNSIGNED NOT NULL DEFAULT 6,
	
	INDEX i_user_id(user_id),
	INDEX i_dev_id(dev_id)
	
	
);