-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(username, email, password)
VALUES (:username, :email, :password)

-- :name update-user! :! :n
-- :doc update an existing user record
UPDATE users
SET username = :username, email = :email
WHERE id = :id

-- :name get-user :? :1
-- :doc retrieve a user given the id.
SELECT * FROM users
WHERE username = :username

-- :name delete-user! :! :n
-- :doc delete a user given the id
DELETE FROM users
WHERE username = :username
