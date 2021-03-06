USE bc_server_db;

CREATE TABLE user_info
(
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    name VARCHAR(128) NOT NULL,
    e_mail VARCHAR(256)  NOT NULL DEFAULT "",
    phone CHAR(32) NOT NULL DEFAULT "",
	is_develop BOOLEAN NOT NULL DEFAULT FALSE,
    password CHAR(64) NOT NULL,
	
	UNIQUE INDEX i_name(name),
	UNIQUE INDEX i_e_mail(e_mail),
	UNIQUE INDEX i_phone(phone)
);
