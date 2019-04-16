/* Create 3 SN which belong to developer "3"("Ansersion3" in user_info) */
/* they are valid for 3 years */

INSERT INTO sn_info (sn, develop_user_id, activite_date, expired_date, exist_time) VALUES("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX1", 3, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 YEAR), 0x7FFFFFFF);
INSERT INTO sn_info (sn, develop_user_id, activite_date, expired_date, exist_time) VALUES("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX2", 3, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 YEAR), 0x7FFFFFFF);
INSERT INTO sn_info (sn, develop_user_id, activite_date, expired_date, exist_time) VALUES("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX3", 3, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 YEAR), 0x7FFFFFFF);

  
