-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(username, nickname, phone_no, password)
VALUES (:username, :nickname, :phone_no, :password)

-- :name update-user-by-id! :! :n
-- :doc update an existing user record
UPDATE users
SET nickname = :nickname, phone_no = :phone_no
WHERE id = :id

-- :name update-password-by-id! :! :n
UPDATE users
SET password = :password
WHERE id = :id

-- :name fetch-user-by-username :? :1
-- :doc retrieve a user given the username.
SELECT * FROM users
WHERE username = :username

-- :name fetch-all-users :? :*
-- :doc retrieve all users.
SELECT id, username, nickname, phone_no FROM users ORDER BY id

-- :name fetch-users-other-than-id :? :*
-- :doc retrieve all users except the given user id
SELECT id, username, nickname, phone_no, admin FROM users
WHERE id <> :id
ORDER BY id

-- :name delete-user-by-id! :! :n
-- :doc delete a user given the id
DELETE FROM users
WHERE id = :id

-- :name fetch-all-users-summary :? :*
-- :doc retrieve all users information.
SELECT
users.id, username, nickname, phone_no,
COALESCE(SUM(rent), 0.00) AS rent,
COALESCE(SUM(internet), 0.00) AS internet,
COALESCE(SUM(others), 0.00) AS others
FROM users LEFT OUTER JOIN transactions ON (users.id = transactions.user_id)
GROUP BY users.id, username, nickname, phone_no
ORDER BY users.id
