(ns rumahsewa141.routes.member
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))

(defn member-page []
  (layout/render "member.html"))

(defroutes member-routes
  (GET "/member" [] (member-page)))
