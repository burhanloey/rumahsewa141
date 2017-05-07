-- :name create-transaction! :! :n
-- :doc create a new transaction record
INSERT INTO transactions
(user_id, rent, internet, others, transaction_timestamp)
VALUES (:user_id, :rent, :internet, :others, CURRENT_TIMESTAMP)

-- :name fetch-all-latest-transactions :? :*
-- :doc retrieve latest transactions given a limit and an offset
SELECT username, rent, internet, others, transaction_timestamp
FROM transactions INNER JOIN users ON (transactions.user_id = users.id)
ORDER BY transaction_timestamp DESC
LIMIT :max_items OFFSET :offset

-- :name fetch-latest-transactions-by-user :? :*
-- :doc retrieve latest transactions given a user, a limit and an offset
SELECT username, rent, internet, others, transaction_timestamp
FROM transactions INNER JOIN users ON (transactions.user_id = users.id)
WHERE user_id = :user_id
ORDER BY transaction_timestamp DESC
LIMIT :max_items OFFSET :offset

-- :name fetch-all-transactions-count :? :1
-- :doc retrieve total count of all transactions
SELECT COUNT(*) AS tcount FROM transactions

-- :name fetch-transactions-count-by-user :? :1
-- :doc retrieve total count of all transactions by user
SELECT COUNT(*) AS tcount FROM transactions
WHERE user_id = :user_id

-- :name fetch-total-bills-by-user :? :1
-- :doc retrieve user bills given the id
SELECT
 COALESCE(SUM(rent), 0.00) AS rent,
 COALESCE(SUM(internet), 0.00) AS internet,
 COALESCE(SUM(others), 0.00) AS others
FROM transactions
WHERE user_id = :user_id
