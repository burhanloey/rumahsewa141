(ns rumahsewa141.routes.member
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [rumahsewa141.db.core :refer [get-user-bills]]
            [rumahsewa141.utils :refer [assoc-total-bills-left]]))

(defn normal-view [section {{id :id username :username} :identity}]
  (let [user-bills (get-user-bills {:id id})]
    (layout/render "member.html" (merge {:username username}
                                        (assoc-total-bills-left user-bills)))))

(defn member-page [{{admin :admin} :identity :as req}]
  (if (true? admin)
    (redirect "/admin")
    (normal-view :overview req)))

(defroutes member-routes
  (GET "/member" req (member-page req))
  (GET "/member/settings" req (member-page req)))
