(ns rumahsewa141.repository.transaction
  (:require [rumahsewa141.db.core :as db]
            [rumahsewa141.util :refer [do-to-selected
                                       parse-int]]))

(defn add-transaction-by-user [sign rent internet others]
  (comp
   (partial db/create-transaction!)
   (partial zipmap [:rent :internet :others :user_id])
   (partial conj [(sign rent) (sign internet) (sign others)])
   (partial parse-int)))

(defn add-transactions-by-users [users sign rent internet others]
  (do-to-selected users (add-transaction-by-user sign rent internet others)))

(defn transactions-count
  ([]
   (db/fetch-all-transactions-count))
  ([{{id :id} :identity}]
   (partial db/fetch-transactions-count-by-user {:user_id id})))

(defn latest-transactions
  [{{id :id admin :admin} :identity}]
  (if (true? admin)
    (partial db/fetch-all-latest-transactions)
    (comp (partial db/fetch-latest-transactions-by-user) (partial merge {:user_id id}))))

(defn user-bills [{{id :id} :identity}]
  (partial db/fetch-total-bills-by-user {:user_id id}))
