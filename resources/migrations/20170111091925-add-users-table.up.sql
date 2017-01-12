CREATE TABLE users
(id SERIAL PRIMARY KEY,
 username VARCHAR(30) UNIQUE NOT NULL CHECK (username <> ''),
 email VARCHAR(30),
 admin BOOLEAN,
 is_active BOOLEAN,
 password VARCHAR(300) NOT NULL CHECK (password <> ''));
