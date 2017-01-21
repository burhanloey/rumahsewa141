CREATE TABLE users
(id SERIAL PRIMARY KEY,
 username VARCHAR(30) UNIQUE NOT NULL CHECK (username <> ''),
 nickname VARCHAR(30),
 phone_no VARCHAR(15),
 admin BOOLEAN,
 password VARCHAR(300) NOT NULL CHECK (password <> ''));
--;;
CREATE TABLE transactions
(id SERIAL PRIMARY KEY,
 user_id INTEGER REFERENCES users (id),
 rent DECIMAL(12,2),
 internet DECIMAL(12,2),
 others DECIMAL(12,2),
 transaction_timestamp TIMESTAMP);