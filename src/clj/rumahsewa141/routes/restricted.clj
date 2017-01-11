(ns rumahsewa141.routes.restricted
  (:require [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]))

(defn restricted-page []
  "Hello, asshole...")

(defroutes restricted-routes
  (GET "/restricted" [] (restricted-page)))
