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
-- :doc retrieve a user given the username.
SELECT * FROM users
WHERE username = :username

-- :name get-users :? :*
-- :doc retrieve all users
SELECT
 id, username, nickname, phone_no,
 COALESCE(SUM(rent_fee), 0.00) AS rent_bill,
 COALESCE(SUM(internet_fee), 0.00) AS internet_bill,
 COALESCE(SUM(other_fees), 0.00) AS other_bills,
 COALESCE(SUM(rent_payment), 0.00) AS rent_payment,
 COALESCE(SUM(internet_payment), 0.00) AS internet_payment,
 COALESCE(SUM(other_payments), 0.00) AS other_payments
FROM
(SELECT 
  t1.id, username, nickname, phone_no, rent_fee, internet_fee, other_fees,
  rent_payment, internet_payment, other_payments
 FROM users t1 LEFT OUTER JOIN bills t2 ON t1.id = t2.user_id
 LEFT OUTER JOIN fees t3 ON t2.fee_id = t3.id
 LEFT OUTER JOIN payments t4 ON t1.id = t4.user_id) AS all_tables
GROUP BY id, username, nickname, phone_no
ORDER BY id

-- :name delete-user! :! :n
-- :doc delete a user given the username
DELETE FROM users
WHERE username = :username

-- :name create-fee! :! :n
-- :doc create a new bill record
INSERT INTO fees
(rent_fee, internet_fee, other_fees, description)
VALUES (:rent_fee, :internet_fee, :other_fees, :description)

-- :name get-fee :? :1
-- :doc retrieve fees given month and year
SELECT year, month, rent_fee, internet_fee, other_fees
FROM bills INNER JOIN fees ON (bills.fees_id = fees.id)
WHERE year = :year AND month = :month

-- :name delete-fee! :! :n
-- :doc delete a fees record given the id
DELETE FROM fees
WHERE id = :id

-- :name create-bill! :! :n
-- :doc create a new bill record
INSERT INTO bills
(user_id, year, month, fee_id)
VALUES (:user_id, :year, :month, :fee_id)

-- :name get-user-bills :? :1
-- :doc retrieve user bills and payments given the id
SELECT
 id,
 COALESCE(SUM(rent_fee), 0.00) AS rent_bill,
 COALESCE(SUM(internet_fee), 0.00) AS internet_bill,
 COALESCE(SUM(other_fees), 0.00) AS other_bills,
 COALESCE(SUM(rent_payment), 0.00) AS rent_payment,
 COALESCE(SUM(internet_payment), 0.00) AS internet_payment,
 COALESCE(SUM(other_payments), 0.00) AS other_payments
FROM
(SELECT 
  t1.id, rent_fee, internet_fee, other_fees,
  rent_payment, internet_payment, other_payments
 FROM users t1 LEFT OUTER JOIN bills t2 ON t1.id = t2.user_id
 LEFT OUTER JOIN fees t3 ON t2.fee_id = t3.id
 LEFT OUTER JOIN payments t4 ON t1.id = t4.user_id
 WHERE t1.id = :id) AS all_user_tables
GROUP BY id

-- :name get-total-bills :? :1
-- :doc retrieve the summation of bills for the user
SELECT
 COALESCE(SUM(rent_fee), 0.00) AS rent_bill,
 COALESCE(SUM(internet_fee), 0.00) AS internet_bill,
 COALESCE(SUM(other_fees), 0.00) AS other_bills
FROM
 (SELECT user_id, rent_fee, internet_fee, other_fees
  FROM bills INNER JOIN fees ON (bills.fee_id = fees.id)) AS fees_listing
WHERE user_id = :user_id

-- :name create-payment! :! :n
-- :doc create a new payment record
INSERT INTO payments
(user_id, rent_payment, internet_payment, other_payments, payment_date)
VALUES (:user_id, :rent_payment, :internet_payment, :other_payments, CURRENT_DATE)

-- :name get-total-payments :? :1
-- :doc retrieve the summation of payment for each bill given the user
SELECT
 COALESCE(SUM(rent_payment), 0.00) AS rent_payment,
 COALESCE(SUM(internet_payment), 0.00) AS internet_payment,
 COALESCE(SUM(other_payments), 0.00) AS other_payments
FROM payments WHERE user_id = :user_id