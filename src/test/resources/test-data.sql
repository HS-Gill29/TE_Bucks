BEGIN TRANSACTION;

DROP TABLE IF EXISTS users, account;

CREATE TABLE users (
	user_id serial NOT NULL,
	username varchar(50) NOT NULL,
	password_hash varchar(200) NOT NULL,
	first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(50),
	role varchar(20),
	CONSTRAINT pk_users PRIMARY KEY (user_id),
	CONSTRAINT uq_username UNIQUE (username)
);

CREATE TABLE account (
	account_id SERIAL PRIMARY KEY,
	user_id int NOT NULL REFERENCES users(user_id),
	balance NUMERIC(20,2) NOT NULL DEFAULT (1000)
);
CREATE TABLE transfer (
	transfer_id SERIAL PRIMARY KEY,
	user_from int NOT NULL REFERENCES users (user_id),
	user_to int NOT NULL REFERENCES users (user_id),
	amount NUMERIC(20,2) CHECK (amount > 0),
	transfer_type VARCHAR(10) NOT NULL CHECK (transfer_type IN ('Send', 'Request')),
	transfer_status VARCHAR(10) NOT NULL CHECK (transfer_status IN ('Approved', 'Pending', 'Rejected'))
);


INSERT INTO users (username,password_hash,role) VALUES ('user1','user1','ROLE_USER'); -- 1
INSERT INTO users (username,password_hash,role) VALUES ('user2','user2','ROLE_USER'); -- 2
INSERT INTO users (username,password_hash,role) VALUES ('user3','user3','ROLE_USER'); -- 3

INSERT INTO account (user_id, balance)
VALUES
    (1, 1000.00),
    (2, 1100.00),
    (3, 1200.00);


INSERT INTO transfer (transfer_id, user_from, user_to, amount, transfer_type, transfer_status)
VALUES (1, 1, 2, 500.00, 'Send', 'Approved');



COMMIT TRANSACTION;
