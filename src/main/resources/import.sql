INSERT INTO beneficiary (id, name) VALUES (100, 'Kamila Nigmetzianova');
INSERT INTO beneficiary (id, name) VALUES (200, 'John Doe');

INSERT INTO account (id, account_number, balance, created_at, pin_code, beneficiary_id) VALUES (100, '7810950923752834', 100.67, current_timestamp, '0ffe1abd1a08215353c233d6e009613e95eec4253832a761af28ff37ac5a150c', 100);
INSERT INTO account (id, account_number, balance, created_at, pin_code, beneficiary_id) VALUES (200, '9872356743828346', 657.0, current_timestamp, '0ffe1abd1a08215353c233d6e009613e95eec4253832a761af28ff37ac5a150c', 100);
INSERT INTO account (id, account_number, balance, created_at, pin_code, beneficiary_id) VALUES (300, '7834687923764628', 21.21, current_timestamp, '0ffe1abd1a08215353c233d6e009613e95eec4253832a761af28ff37ac5a150c', 200);

INSERT INTO transaction (id, account_id, type, balance_before, balance_after, created_at) VALUES (100, 100, 'DEPOSIT', 20.67, 100.67, current_timestamp);
INSERT INTO transaction (id, account_id, type, balance_before, balance_after, created_at) VALUES (200, 200, 'WITHDRAW', 80.0, 21.21, current_timestamp);
