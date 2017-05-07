(ns rumahsewa141.repository.config
  (:require [rumahsewa141.db.core :as db]))

(defn registration-allowed? []
  {:allowed (:value (db/get-registration-config))})
