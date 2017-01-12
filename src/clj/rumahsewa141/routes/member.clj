(ns rumahsewa141.routes.member
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]))

(defn member-page [{{username :username :as identity} :identity}]
  (layout/render "member.html" {:name username}))

(defroutes member-routes
  (GET "/member" req (member-page req)))
