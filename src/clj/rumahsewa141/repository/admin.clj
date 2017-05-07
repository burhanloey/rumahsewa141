(ns rumahsewa141.repository.admin
  (:require [rumahsewa141.db.core :as db]))

(defn display-info [{:keys [username nickname phone_no]}]
  {:name (if (clojure.string/blank? nickname)
           username
           nickname)
   :contact_no phone_no})

(defn display-all-admins-info []
  (map display-info (db/get-all-admins)))
