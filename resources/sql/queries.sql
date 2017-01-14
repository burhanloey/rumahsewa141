-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(username, nickname, phone_no, password)
VALUES (:username, :nickname, :phone_no, :password)

-- :name update-user! :! :n
-- :doc update an existing user record
UPDATE users
SET username = :username, nickname = :nickname, phone_no = :phone_no
WHERE id = :id

-- :name get-user :? :1
-- :doc retrieve a user given the id.
SELECT * FROM users
WHERE username = :username;

-- :name delete-user! :! :n
-- :doc delete a user given the username
DELETE FROM users
WHERE username = :username

-- :name create-bills! :! :n
-- :doc create a new bill record
INSERT INTO bills
(rental_fee, internet_bill, other_bills, description)
VALUES (:rental_fee, :internet_bill, :other_bills, :description)

-- :name get-bills :? :1
-- :doc retrieve bills given month and year
SELECT year, month, rental_fee, internet_bill, other_bills
FROM membership INNER JOIN bills ON (membership.bills_id = bills.id)
WHERE year = :year AND month = :month

-- :name delete-bills! :! :n
-- :doc delete a bills record given the id
DELETE FROM bills
WHERE id = :id

-- :name create-membership! :! :n
-- :doc create a new membersip record
INSERT INTO membership
(user_id, year, month, bills_id)
VALUES (:user_id, :year, :month, :bills_id)

-- :name get-membership-fees :? :1
-- :doc retrieve the summation of bills for the user
SELECT
 COALESCE(SUM(rental_fee), 0.00) AS rental_fee,
 COALESCE(SUM(internet_bill), 0.00) AS internet_bill,
 COALESCE(SUM(other_bills), 0.00) AS other_bills
FROM
 (SELECT user_id, rental_fee, internet_bill, other_bills
  FROM membership INNER JOIN bills ON (membership.bills_id = bills.id)) AS bills_listing
WHERE user_id = :user_id

-- :name create-payment! :! :n
-- :doc create a new payment record
INSERT INTO payments
(user_id, rent_payment, internet_payment, other_payment, payment_date)
VALUES (:user_id, :rent_payment, :internet_payment, :other_payment, CURRENT_DATE)

-- :name get-total-payments :? :1
-- :doc retrieve the summation of payment for each bill given the user
SELECT
 COALESCE(SUM(rent_payment), 0.00) AS rent_payment,
 COALESCE(SUM(internet_payment), 0.00) AS internet_payment,
 COALESCE(SUM(other_payment), 0.00) AS other_payment
FROM payments WHERE user_id = :user_id