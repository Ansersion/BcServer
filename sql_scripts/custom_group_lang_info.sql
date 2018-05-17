USE bc_server_db;

CREATE TABLE custom_group_lang_info
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	group_lang VARCHAR(128) NOT NULL default "",
	
	UNIQUE INDEX i_group_lang(group_lang)
);