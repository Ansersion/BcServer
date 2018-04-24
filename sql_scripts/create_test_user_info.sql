/* Create 3 users: Ansersion1, Ansersion2, Ansersion3*/
/* 3 sha256 of passwords: ansersion1, ansersion2, ansersion3 */
/* "@@@@?n" means null */
INSERT INTO user_info (name, e_mail, phone, password) VALUES("Ansersion1", "@@@@?1", "@@@@?1", "1a7d8f0ae2600087a30fda710f1bfd655287f4ee609bc654c634bce355a6fd41");
INSERT INTO user_info (name, e_mail, phone, password) VALUES("Ansersion2", "@@@@?2", "@@@@?2", "e8a3486b600b41c8336ae755550df518670f2aa19d61e77b64c60891f08e89a8");
INSERT INTO user_info (name, e_mail, phone, is_develop, password) VALUES("Ansersion3", "@@@@?3", "@@@@?3", true, "357f30611fe5930e1b7b5638e192aeb7c08930e20d8d96bcbd76c831715cd227");
