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
	account_balance NUMERIC(20,2) NOT NULL DEFAULT (1000)
);

CREATE TABLE transfer (
	transfer_id SERIAL PRIMARY KEY,
	account_sending_money int NOT NULL REFERENCES account(account_id),
	account_receiving_money int NOT NULL REFERENCES account(account_id),
	account_initiating_transfer int NOT NULL REFERENCES account(account_id),
	transfer_amount NUMERIC(6,2) CHECK (transfer_amount > 0),
	transfer_status VARCHAR(10) NOT NULL CHECK (transfer_status IN ('Approved', 'Pending', 'Rejected'))
);

COMMIT TRANSACTION;
