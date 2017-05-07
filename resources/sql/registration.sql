-- :name fetch-registration-config :? :1
-- :doc Retrieve current status of registration
SELECT value FROM config
WHERE name = 'allow_registration'

-- :name update-registration-config! :! :n
-- :doc Update registration config for the website
UPDATE config SET value = :value
WHERE name = 'allow_registration'
