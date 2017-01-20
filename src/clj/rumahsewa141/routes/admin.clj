(ns rumahsewa141.routes.admin
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [rumahsewa141.db.core :refer [get-users
                                          get-user-bills]]
            [rumahsewa141.utils :refer [assoc-total-bills-left]]))

(defn get-all-users-bills-left []
  (let [users (get-users)]
    {:users (map assoc-total-bills-left users)}))

(defn manage-page [{{username :username} :identity}]
  (layout/render "member.html" {:username username
                                :admin true
                                :manage true}))

(defn billing-page [{{username :username} :identity}]
  (layout/render "member.html" {:username username
                                :admin true
                                :billing true}))

(defn admin-page [{{username :username} :identity}]
  (layout/render "member.html" (merge {:username username
                                       :admin true
                                       :overview true}
                                      (get-all-users-bills-left))))

(defroutes admin-routes
  (GET "/admin" req (admin-page req))
  (GET "/admin/billing" req (billing-page req))
  (GET "/admin/manage" req (manage-page req))
  (GET "/admin/settings" [] ""))
