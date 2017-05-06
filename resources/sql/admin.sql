-- :name update-user-status! :! :n
-- :doc update user admin status given the user id
UPDATE users
SET admin = :admin
WHERE id = :id

-- :name get-all-admins :? :*
-- :doc retrieve all administrators
SELECT id, username, nickname, phone_no FROM users
WHERE admin = TRUE ORDER BY id
