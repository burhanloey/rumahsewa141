(ns rumahsewa141.services.admin
  (:require [rumahsewa141.db.core :as db]))

(defn display-info
  "Display user info in hashmap. The value for :name is the nicname
  if it exists. If not, just use the username."
  [{:keys [username nickname phone_no]}]
  {:name (if (clojure.string/blank? nickname)
           username
           nickname)
   :contact_no phone_no})

(defn display-all-admins-info
  "Apply display-info function for all administrators."
  []
  (map display-info (db/fetch-all-admins)))
