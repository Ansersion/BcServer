use bc_server_db;
create table sys_sig_tab
(
	sys_sig_tab_id int unsigned not null auto_increment primary key,
    sys_basic blob,
    sys_temp blob,
    sys_clean blob
);

