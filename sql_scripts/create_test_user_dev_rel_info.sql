/* Create 3 SN which belong to developer "3"("Ansersion3" in user_info) */
/* they are valid for 3 years */

/* User "2" can use device "1" which belongs to user "1", and user "2" can "read and control"(6) */
INSERT INTO user_dev_rel_info (user_id, dev_id, auth) VALUES(2, 1, 6);

/* User "3" can use device "1" which belongs to user "1", and user "3" can "only read(4) */
INSERT INTO user_dev_rel_info (user_id, dev_id, auth) VALUES(3, 1, 4);