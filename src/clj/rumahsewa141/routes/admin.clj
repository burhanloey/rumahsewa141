(ns rumahsewa141.routes.admin
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [rumahsewa141.db.core :refer [*db*
                                          get-fees
                                          get-users
                                          get-all-users-info
                                          create-bill!]]
            [rumahsewa141.utils :refer [assoc-total-bills-left
                                        assoc-fee-display]]
            [conman.core :refer [with-transaction]]
            [clojure.java.jdbc :as jdbc]))

(defn get-all-users-bills-left []
  (let [users (get-all-users-info)]
    (map assoc-total-bills-left users)))

(defn get-all-fees []
  (let [fees (get-fees)]
    (map assoc-fee-display fees)))

(defn issue-bills [{{:keys [users month year fee] :as params} :params}]
  (if (nil? users)
    "Select at least one user to issue bills."
    (if-let [_ (doall
                (map (fn [user]
                       (create-bill! {:user_id (Integer/parseInt user)
                                      :year (Integer/parseInt year)
                                      :month (Integer/parseInt  month)
                                      :fee_id (Integer/parseInt fee)}))
                     (flatten (vector users))))]
      (redirect "/admin"))))

(defn manage-page [{{username :username} :identity}]
  (layout/render "member.html" {:username username
                                :admin true
                                :manage true}))

(defn billing-page [{{username :username} :identity}]
  (layout/render "member.html" {:username username
                                :admin true
                                :billing true
                                :users (get-users)
                                :months (take 12 (iterate inc 1))
                                :years (take 20 (iterate inc 2017))
                                :fees (get-all-fees)}))

(defn admin-page [{{username :username} :identity}]
  (layout/render "member.html" {:username username
                                :admin true
                                :overview true
                                :users (get-all-users-bills-left)}))

(defroutes admin-routes
  (GET "/admin" req (admin-page req))
  (GET "/admin/billing" req (billing-page req))
  (POST "/admin/billing" req (issue-bills req))
  (GET "/admin/manage" req (manage-page req))
  (GET "/admin/settings" [] ""))
