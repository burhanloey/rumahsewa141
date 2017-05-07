(ns rumahsewa141.repository.config
  (:require [rumahsewa141.db.core :as db]))

(defn registration-allowed? []
  {:allowed (:value (db/fetch-registration-config))})

(defn update-registration-config [action]
  (case action
    "allow" (db/update-registration-config! {:value true})
    "close" (db/update-registration-config! {:value false})))
