-- :name get-registration-config :? :1
-- :doc Retrieve current status of registration
SELECT value FROM config
WHERE name = 'allow_registration'

-- :name allow-registration :! :n
-- :doc Allow registration for the website
UPDATE config SET value = TRUE
WHERE name = 'allow_registration'

-- :name close-registration :! :n
-- :doc Close registration for the website
UPDATE config SET value = FALSE
WHERE name = 'allow_registration'
