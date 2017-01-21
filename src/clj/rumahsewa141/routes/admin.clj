(ns rumahsewa141.routes.admin
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [rumahsewa141.db.core :refer [get-all-users
                                          get-all-users-info
                                          create-transaction!]]
            [rumahsewa141.utils :refer [assoc-total-bills-left
                                        assoc-fee-display]]))

(defn parse-double [num]
  (if (clojure.string/blank? num)
    0.00
    (Double/parseDouble num)))

(defn do-transaction [sign {{:keys [users rent internet others]} :params}]
  (if (nil? users)
    "Please select a user."
    (if-let [_ (doall
                (map (fn [user]
                       (create-transaction!
                        {:user_id (Integer/parseInt user)
                         :rent (sign (parse-double rent))
                         :internet (sign (parse-double internet))
                         :others (sign (parse-double others))}))
                     (flatten (vector users))))]
      (redirect "/admin"))))

(defn manage-page [{{username :username} :identity}]
  (layout/render "member.html" {:username username
                                :admin true
                                :manage true}))

(defn payment-page [{{username :username} :identity}]
  (layout/render "member.html" {:username username
                                :admin true
                                :payment true
                                :users (get-all-users)}))

(defn billing-page [{{username :username} :identity}]
  (layout/render "member.html" {:username username
                                :admin true
                                :billing true
                                :users (get-all-users)}))

(defn admin-page [{{username :username} :identity}]
  (layout/render "member.html" {:username username
                                :admin true
                                :overview true
                                :users (get-all-users-info)}))

(defroutes admin-routes
  (GET "/admin" req (admin-page req))
  (GET "/admin/billing" req (billing-page req))
  (POST "/admin/billing" req (do-transaction + req))
  (GET "/admin/payment" req (payment-page req))
  (POST "/admin/payment" req (do-transaction - req))
  (GET "/admin/manage" req (manage-page req))
  (GET "/admin/settings" [] ""))
