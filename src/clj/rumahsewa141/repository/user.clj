(ns rumahsewa141.repository.user
  (:require [rumahsewa141.db.core :as db]
            [rumahsewa141.util :refer [do-to-selected]]
            [buddy.hashers :as hashers]))

(def default-value {:nickname ""
                    :phone_no ""})

(defn create-user [username password nickname phone_no]
  (db/create-user! (merge default-value {:username username
                                         :password (hashers/encrypt password)
                                         :nickname nickname
                                         :phone_no phone_no})))

(defn user-bills [{{id :id} :identity}]
  (partial db/get-user-bills {:user_id id}))

(defn user-info [{{username :username} :identity}]
  (partial db/get-user {:username username}))

(defn all-users []
  {:users (db/get-all-users)})

(defn other-users [{{id :id} :identity}]
  (comp (partial assoc {} :users) (partial db/get-other-users {:id id})))

(defn- assoc-index [users index]
  (assoc users :index index))

(defn all-users-summary []
  (let [users-summary (db/get-all-users-summary)
        index         (iterate inc 1)]
    {:users (map assoc-index users-summary index)}))

(defn lookup-user [username password]
  (if-let [user (db/get-user {:username username})]
    (if (hashers/check password (get user :password))
      (dissoc user :password))))

(defn wrong-password? [username password]
  (when-let [user (db/get-user {:username username})]
    (not (hashers/check password (get user :password)))))

(defn update-users [users action]
  (do-to-selected users (if (= action "delete")
                          #(db/delete-user!
                            {:id (Integer/parseInt %)})
                          #(db/update-user-status!
                            {:id (Integer/parseInt %)
                             :admin (case action
                                      "assign" true
                                      "revoke" false)}))))

(defn update-user-info [id nickname phone_no]
  (db/update-user! {:id id
                    :nickname nickname
                    :phone_no phone_no}))

(defn change-user-password [id new]
  (db/change-password! {:id id
                        :password (hashers/encrypt new)}))
