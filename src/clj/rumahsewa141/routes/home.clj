(ns rumahsewa141.routes.home
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [rumahsewa141.db.core :as db]))

(defn home-page []
  (layout/render "home.html"))

(defn display-info [{:keys [username nickname phone_no]}]
  {:name (if (clojure.string/blank? nickname)
           username
           nickname)
   :contact_no phone_no})

(defn about-page []
  (layout/render "about.html" {:admins (map display-info (db/get-all-admins))}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page)))
