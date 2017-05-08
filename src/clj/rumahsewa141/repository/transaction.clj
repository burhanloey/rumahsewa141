(ns rumahsewa141.repository.transaction
  (:require [rumahsewa141.db.core :as db]
            [rumahsewa141.util :refer [do-to-selected
                                       parse-int]]))

(defn add-transaction-by-user
  "Return a function that will create transaction given the bill
  values. The sign determines the type of transaction,
  + for billed,
  - for paid."
  [sign rent internet others]
  (comp
   (partial db/create-transaction!)
   (partial zipmap [:rent :internet :others :user_id])
   (partial conj [(sign rent) (sign internet) (sign others)])
   (partial parse-int)))

(defn add-transactions-by-users
  "Apply add-transaction-by-user to all selected users."
  [users sign rent internet others]
  (do-to-selected users (add-transaction-by-user sign rent internet others)))

(defn transactions-count
  "Fetch all transactions count for all users. If identity supplied,
  return a function that fetch all transactions count for that user."
  ([]
   (db/fetch-all-transactions-count))
  ([{{id :id} :identity}]
   (partial db/fetch-transactions-count-by-user {:user_id id})))

(defn latest-transactions
  "Return a function that will fetch latest transactions for all
  users if requested by admin, else, fetch latest transactions for
  that user."
  [{{id :id admin :admin} :identity}]
  (if (true? admin)
    (partial db/fetch-all-latest-transactions)
    (comp (partial db/fetch-latest-transactions-by-user) (partial merge {:user_id id}))))

(defn user-bills
  "Return a function that will fetch total bills for the user."
  [{{id :id} :identity}]
  (partial db/fetch-total-bills-by-user {:user_id id}))
