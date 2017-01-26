(ns rumahsewa141.routes.home
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/render "home.html" {:docs "Best landing page here."}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (layout/render "about.html")))
