CREATE TABLE users
(id SERIAL PRIMARY KEY,
 username VARCHAR(30) UNIQUE NOT NULL CHECK (username <> ''),
 nickname VARCHAR(30),
 phone_no VARCHAR(15),
 admin BOOLEAN,
 password VARCHAR(300) NOT NULL CHECK (password <> ''));
--;;
CREATE TABLE bills
(id SERIAL PRIMARY KEY,
 rental_fee DECIMAL(12,2),
 internet_bill DECIMAL(12,2),
 other_bills DECIMAL(12,2),
 description VARCHAR(150));
--;;
CREATE TABLE membership
(id SERIAL PRIMARY KEY,
 user_id INTEGER REFERENCES users (id),
 year INTEGER CHECK (year BETWEEN 2000 AND 2100),
 month INTEGER CHECK (month BETWEEN 1 AND 12),
 bills_id INTEGER REFERENCES bills (id));
--;;
CREATE UNIQUE INDEX membership_yearmonth ON membership (year, month);
--;;
CREATE TABLE payments
(id SERIAL PRIMARY KEY,
 user_id INTEGER REFERENCES users (id),
 rent_payment DECIMAL(12,2),
 internet_payment DECIMAL(12,2),
 other_payment DECIMAL(12,2),
 payment_date DATE);