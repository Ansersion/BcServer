create database bc_server_db character set utf8;
use bc_server_db;
create table user_info
(
    user_id int unsigned not null auto_increment primary key,
    name varchar(128) null default "",
    e_mail varchar(256)  null default "",
    phone varchar(128) null default "",
    password char(32) not null
);
insert into user_info (name, password) values("Ansersion", "a123456");
