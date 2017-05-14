(ns rumahsewa141.routes.home
  (:require [rumahsewa141.layout :as layout]
            [rumahsewa141.config :refer [env]]
            [rumahsewa141.services.admin :as admin]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]))

(defn home-page
  "Render home page."
  []
  (layout/render "home.html" {:address1 (:address1 env)
                              :address2 (:address2 env)}))

(defn about-page
  "Render about page with all administrators contact retrieved from
  database."
  []
  (layout/render "about.html" {:admins (admin/display-all-admins-info)}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page)))
