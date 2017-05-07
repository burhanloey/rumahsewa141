(ns rumahsewa141.repository.user
  (:require [rumahsewa141.db.core :as db]
            [buddy.hashers :as hashers]))

(defn user-bills [{{id :id} :identity}]
  #(db/get-user-bills {:user_id id}))

(defn user-info [{{username :username} :identity}]
  #(db/get-user {:username username}))

(defn all-users []
  {:users (db/get-all-users)})

(defn other-users [{{id :id} :identity}]
  (fn [] {:users (db/get-other-users {:id id})}))

(defn all-users-summary []
  (let [users-summary (db/get-all-users-summary)
        index         (iterate inc 1)]
    {:users (map #(assoc %1 :index %2) users-summary index)}))

(defn lookup-user [username password]
  (if-let [user (db/get-user {:username username})]
    (if (hashers/check password (get user :password))
      (dissoc user :password))))

(defn wrong-password? [username password]
  (when-let [user (db/get-user {:username username})]
    (not (hashers/check password (get user :password)))))
