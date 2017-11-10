use bc_server_db;
create table sys_sig_tab
(
	sys_sig_tab_id int unsigned not null auto_increment primary key,
    sys_basic varchar(128) not null default "",
    sys_temp varchar(128) not null default "",
    sys_clean varchar(128) not null default ""
);

