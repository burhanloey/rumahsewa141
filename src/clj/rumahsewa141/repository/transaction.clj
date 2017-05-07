(ns rumahsewa141.repository.transaction
  (:require [rumahsewa141.db.core :as db]))

(defn transactions-count
  ([]
   (db/get-transactions-count))
  ([{{id :id} :identity}]
   (partial db/get-transactions-count-by-user {:user_id id})))

(defn latest-transactions
  [{{id :id admin :admin} :identity}]
  (if (true? admin)
    (partial db/get-latest-transactions)
    (comp (partial db/get-latest-transactions-by-user) (partial merge {:user_id id}))))
