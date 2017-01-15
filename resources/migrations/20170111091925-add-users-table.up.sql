CREATE TABLE users
(id SERIAL PRIMARY KEY,
 username VARCHAR(30) UNIQUE NOT NULL CHECK (username <> ''),
 nickname VARCHAR(30),
 phone_no VARCHAR(15),
 admin BOOLEAN,
 password VARCHAR(300) NOT NULL CHECK (password <> ''));
--;;
CREATE TABLE fees
(id SERIAL PRIMARY KEY,
 rent_fee DECIMAL(12,2),
 internet_fee DECIMAL(12,2),
 other_fees DECIMAL(12,2),
 description VARCHAR(150));
--;;
CREATE TABLE bills
(id SERIAL PRIMARY KEY,
 user_id INTEGER REFERENCES users (id),
 year INTEGER CHECK (year BETWEEN 2000 AND 2100),
 month INTEGER CHECK (month BETWEEN 1 AND 12),
 fee_id INTEGER REFERENCES fees (id));
--;;
CREATE UNIQUE INDEX bills_yearmonth ON bills (user_id, year, month);
--;;
CREATE TABLE payments
(id SERIAL PRIMARY KEY,
 user_id INTEGER REFERENCES users (id),
 rent_payment DECIMAL(12,2),
 internet_payment DECIMAL(12,2),
 other_payments DECIMAL(12,2),
 payment_date DATE);