(ns rumahsewa141.services.user
  (:require [rumahsewa141.db.core :as db]
            [rumahsewa141.util :refer [do-to-selected
                                       parse-int]]
            [buddy.hashers :as hashers]))

(def default-value {:nickname ""
                    :phone_no ""})

(defn create-user
  "Create user with all the details. The details will be merged with
  the default values which is an empty string."
  [username password nickname phone_no]
  (db/create-user! (merge default-value {:username username
                                         :password (hashers/encrypt password)
                                         :nickname nickname
                                         :phone_no phone_no})))

(defn user-info
  "Return a function that will fetch user given the username."
  [{{username :username} :identity}]
  (partial db/fetch-user-by-username {:username username}))

(defn lookup-user
  "Look up the user given the username, and then check whether the
  password matches the supplied password. If correct, return the user."
  [username password]
  (when-let [user (db/fetch-user-by-username {:username username})]
    (when (hashers/check password (get user :password))
      (dissoc user :password))))

(defn wrong-password?
  "Check whether the supplied password matches the user's password."
  [username password]
  (when-let [user (db/fetch-user-by-username {:username username})]
    (not (hashers/check password (get user :password)))))

(defn all-users
  "Return all users."
  []
  {:users (db/fetch-all-users)})

(defn other-users
  "Return a function that will fetch all users except the supplied
  identity."
  [{{id :id} :identity}]
  (comp
   (partial assoc {} :users)
   (partial db/fetch-users-other-than-id {:id id})))

(defn- assoc-index
  "Associate a hashmap of user details with the index value."
  [users index]
  (assoc users :index index))

(defn all-users-summary
  "Fetch all users summary with index associated with it."
  []
  (let [users-summary (db/fetch-all-users-summary)
        index         (iterate inc 1)]
    {:users (map assoc-index users-summary index)}))

(defn delete-user
  "Return a function that will delete user given the id."
  []
  (comp
   (partial db/delete-user-by-id!)
   (partial assoc {} :id)
   (partial parse-int)))

(defn update-admin-status
  "Update user status given the id with the value from 'admin?'."
  [admin?]
  (comp
   (partial db/update-user-status!)
   (partial assoc {} :admin admin? :id)
   (partial parse-int)))

(defn assign-admin-by-id
  "Update admin status to true."
  []
  (update-admin-status true))

(defn revoke-admin-by-id
  "Update admin status to false."
  []
  (update-admin-status false))

(defn update-users
  "Update selected users according to the action.
  'delete' action will delete the user,
  'assign' action will update user to admin, and
  'revoke' action will update user to normal user."
  [users action]
  (do-to-selected users (case action
                          "delete" (delete-user)
                          "assign" (assign-admin-by-id)
                          "revoke" (revoke-admin-by-id))))

(defn update-user-info
  "Update user info with the supplied values."
  [id nickname phone_no]
  (db/update-user-by-id! {:id id
                          :nickname nickname
                          :phone_no phone_no}))

(defn change-user-password
  "Update user's password with the new password."
  [id new]
  (db/update-password-by-id! {:id id
                              :password (hashers/encrypt new)}))
