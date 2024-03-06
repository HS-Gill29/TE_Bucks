BEGIN TRANSACTION;

DROP TABLE IF EXISTS users, account, transfer;

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
	user_from int NOT NULL REFERENCES account(account_id),
	user_to int NOT NULL REFERENCES account(account_id),
	amount NUMERIC(6,2) CHECK (amount > 0),
	transfer_type VARCHAR(10) NOT NULL CHECK (transfer_type IN ('Send', 'Request')),
	transfer_status VARCHAR(10) NOT NULL CHECK (transfer_status IN ('Approved', 'Pending', 'Rejected'))
);

COMMIT TRANSACTION;
