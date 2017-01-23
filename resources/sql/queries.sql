-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(username, nickname, phone_no, password)
VALUES (:username, :nickname, :phone_no, :password)

-- :name update-user! :! :n
-- :doc update an existing user record
UPDATE users
SET nickname = :nickname, phone_no = :phone_no
WHERE id = :id

-- :name update-user-status! :! :n
-- :doc update user admin status given the user id
UPDATE users
SET admin = :admin
WHERE id = :id

-- :name get-user :? :1
-- :doc retrieve a user given the username.
SELECT * FROM users
WHERE username = :username

-- :name get-all-users :? :*
-- :doc retrieve all users.
SELECT id, username, nickname, phone_no FROM users ORDER BY id

-- :name get-other-users :? :*
-- :doc retrieve all users except the given user id
SELECT id, username, nickname, phone_no, admin FROM users
WHERE id <> :id
ORDER BY id

-- :name delete-user! :! :n
-- :doc delete a user given the username
DELETE FROM users
WHERE username = :username

-- :name create-transaction! :! :n
-- :doc create a new transaction record
INSERT INTO transactions
(user_id, rent, internet, others, transaction_timestamp)
VALUES (:user_id, :rent, :internet, :others, CURRENT_TIMESTAMP)

-- :name get-user-bills :? :1
-- :doc retrieve user bills given the id
SELECT
 COALESCE(SUM(rent), 0.00) AS rent,
 COALESCE(SUM(internet), 0.00) AS internet,
 COALESCE(SUM(others), 0.00) AS others
FROM transactions
WHERE user_id = :user_id

-- :name get-all-users-summary :? :*
-- :doc retrieve all users information.
SELECT
 users.id, username, nickname, phone_no,
 COALESCE(SUM(rent), 0.00) AS rent,
 COALESCE(SUM(internet), 0.00) AS internet,
 COALESCE(SUM(others), 0.00) AS others
FROM users LEFT OUTER JOIN transactions ON (users.id = transactions.user_id)
GROUP BY users.id, username, nickname, phone_no
ORDER BY users.id