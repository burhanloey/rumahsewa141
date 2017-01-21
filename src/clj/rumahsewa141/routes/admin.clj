(ns rumahsewa141.routes.admin
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [rumahsewa141.db.core :refer [get-all-users
                                          get-all-users-info
                                          create-transaction!]]))

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

(defn all-users []
  {:users (get-all-users)})

(defn all-users-info []
  {:users (get-all-users-info)})

(defn admin-page [section get-content-fn {{username :username} :identity}]
  (layout/render "member.html" {:username username
                                :admin true
                                :section section
                                :content (get-content-fn)}))

(defroutes admin-routes
  (GET "/admin" req (admin-page "overview" all-users-info req))
  (GET "/admin/billing" req (admin-page "billing" all-users req))
  (GET "/admin/payment" req (admin-page "payment" all-users req))
  (GET "/admin/manage" req (admin-page "manage" all-users req))
  (POST "/admin/billing" req (do-transaction + req))
  (POST "/admin/payment" req (do-transaction - req)))
