(ns rumahsewa141.repository.transaction
  (:require [rumahsewa141.db.core :as db]))

(defn transactions-count
  ([]
   (db/get-transactions-count))
  ([{{id :id} :identity}]
   #(db/get-transactions-count-by-user {:user_id id})))

(defn latest-transactions
  [{{id :id admin :admin} :identity}]
  (if (true? admin)
    #(db/get-latest-transactions %)
    #(db/get-latest-transactions-by-user (merge % {:user_id id}))))
