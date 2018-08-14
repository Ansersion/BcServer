USE bc_server_db;

CREATE TABLE custom_signal_enum_lang_entity_info
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	chinese VARCHAR(128) NOT NULL default "",
	english VARCHAR(128) NOT NULL default "",
	french VARCHAR(128) NOT NULL default "",
	russian VARCHAR(128) NOT NULL default "",
	arabic VARCHAR(128) NOT NULL default "",
	spanish VARCHAR(128) NOT NULL default ""
	
);
