/* Create 3 devices which belongs to 3 users */
/* 0x0000(0): device name(F) */
/* 0xE000(57344): serial number(F) */
/* 0xE001(57345): communication state(F) */
INSERT INTO signal_info (signal_id, dev_id) VALUES(0, 1);
INSERT INTO signal_info (signal_id, dev_id) VALUES(57344, 1);
INSERT INTO signal_info (signal_id, dev_id) VALUES(57345, 1);
INSERT INTO signal_info (signal_id, dev_id) VALUES(0, 2);
INSERT INTO signal_info (signal_id, dev_id) VALUES(57344, 2);
INSERT INTO signal_info (signal_id, dev_id) VALUES(57345, 2);
INSERT INTO signal_info (signal_id, dev_id) VALUES(0, 3);
INSERT INTO signal_info (signal_id, dev_id) VALUES(1, 3);
INSERT INTO signal_info (signal_id, dev_id) VALUES(2, 3);
INSERT INTO signal_info (signal_id, dev_id) VALUES(57344, 3);
INSERT INTO signal_info (signal_id, dev_id) VALUES(57345, 3);
