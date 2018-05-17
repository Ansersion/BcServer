USE bc_server_db;

CREATE TABLE custom_unit_lang_info
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	unit_lang CHAR(32) NOT NULL,
	
	UNIQUE INDEX i_unit_lang(unit_lang)
);