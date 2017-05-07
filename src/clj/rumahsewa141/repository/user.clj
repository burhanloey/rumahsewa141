(ns rumahsewa141.repository.user
  (:require [rumahsewa141.db.core :as db]
            [rumahsewa141.util :refer [do-to-selected
                                       parse-int]]
            [buddy.hashers :as hashers]))

(def default-value {:nickname ""
                    :phone_no ""})

(defn create-user [username password nickname phone_no]
  (db/create-user! (merge default-value {:username username
                                         :password (hashers/encrypt password)
                                         :nickname nickname
                                         :phone_no phone_no})))

(defn user-info [{{username :username} :identity}]
  (partial db/fetch-user-by-username {:username username}))

(defn lookup-user [username password]
  (if-let [user (db/fetch-user-by-username {:username username})]
    (if (hashers/check password (get user :password))
      (dissoc user :password))))

(defn wrong-password? [username password]
  (when-let [user (db/fetch-user-by-username {:username username})]
    (not (hashers/check password (get user :password)))))

(defn all-users []
  {:users (db/fetch-all-users)})

(defn other-users [{{id :id} :identity}]
  (comp
   (partial assoc {} :users)
   (partial db/fetch-users-other-than-id {:id id})))

(defn- assoc-index [users index]
  (assoc users :index index))

(defn all-users-summary []
  (let [users-summary (db/fetch-all-users-summary)
        index         (iterate inc 1)]
    {:users (map assoc-index users-summary index)}))

(defn delete-user []
  (comp
   (partial db/delete-user-by-id!)
   (partial assoc {} :id)
   (partial parse-int)))

(defn update-admin-status [admin?]
  (comp
   (partial db/update-user-status!)
   (partial assoc {} :admin admin? :id)
   (partial parse-int)))

(defn assign-admin-by-id []
  (update-admin-status true))

(defn revoke-admin-by-id []
  (update-admin-status false))

(defn update-users [users action]
  (do-to-selected users (case action
                          "delete" (delete-user)
                          "assign" (assign-admin-by-id)
                          "revoke" (revoke-admin-by-id))))

(defn update-user-info [id nickname phone_no]
  (db/update-user-by-id! {:id id
                          :nickname nickname
                          :phone_no phone_no}))

(defn change-user-password [id new]
  (db/update-password-by-id! {:id id
                              :password (hashers/encrypt new)}))
