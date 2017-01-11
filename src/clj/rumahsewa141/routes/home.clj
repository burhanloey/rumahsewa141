(ns rumahsewa141.routes.home
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/render
   "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn do-login! []
  )

(defn login-page [{session :session}]
  (-> (redirect "/restricted")
      (assoc :session (assoc session :identity "dingdong"))))

(defn logout-page [{session :session}]
  (-> (redirect "/")
      (assoc :session (dissoc session :identity))))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/login" req (login-page req))
  (GET "/logout" req (logout-page req))
  (GET "/about" [] (about-page)))
