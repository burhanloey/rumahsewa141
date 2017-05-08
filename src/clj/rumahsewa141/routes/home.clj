(ns rumahsewa141.routes.home
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [rumahsewa141.repository.admin :refer [display-all-admins-info]]))

(defn home-page
  "Render home page."
  []
  (layout/render "home.html"))

(defn about-page
  "Render about page with all administrators contact retrieved from
  database."
  []
  (layout/render "about.html" {:admins (display-all-admins-info)}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page)))
