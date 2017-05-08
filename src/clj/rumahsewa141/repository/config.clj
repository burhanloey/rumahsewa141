(ns rumahsewa141.repository.config
  (:require [rumahsewa141.db.core :as db]))

(defn registration-allowed?
  "Check whether website registration is allowed. The value returned
  is in a hashmap."
  []
  {:allowed (:value (db/fetch-registration-config))})

(defn update-registration-config
  "Set allow_registration value to true for 'allow', and false for 'close'."
  [action]
  (case action
    "allow" (db/update-registration-config! {:value true})
    "close" (db/update-registration-config! {:value false})))
