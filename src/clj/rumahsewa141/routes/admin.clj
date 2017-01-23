(ns rumahsewa141.routes.admin
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [rumahsewa141.db.core :as db]))

(defn parse-double [num]
  (if (clojure.string/blank? num)
    0.00
    (Double/parseDouble num)))

(defn do-to-selected [users f]
  (doall (map f (flatten (vector users)))))

(defn do-transaction [sign {{:keys [users rent internet others]} :params}]
  (if (nil? users)
    "Please select a user."
    (if-let [_ (do-to-selected users #(db/create-transaction!
                                       {:user_id (Integer/parseInt %)
                                        :rent (sign (parse-double rent))
                                        :internet (sign (parse-double internet))
                                        :others (sign (parse-double others))}))]
      (redirect "/admin"))))

(defn do-manage [{{:keys [users action]} :params}]
  (if (nil? users)
    "Please select a user."
    (if-let [_ (do-to-selected users #(db/update-user-status!
                                       {:id (Integer/parseInt %)
                                        :admin (case action
                                                 "assign" true
                                                 "revoke" false)}))]
      (redirect "/admin/manage"))))

(defn do-update-user [{{id :id} :identity {:keys [nickname phone_no]} :params}]
  (if-let [_ (db/update-user!
              {:id id
               :nickname nickname
               :phone_no phone_no})]
    (redirect "/admin/settings")))

(defn all-users []
  {:users (db/get-all-users)})

(defn other-users [{{id :id} :identity}]
  (fn [] {:users (db/get-other-users {:id id})}))

(defn all-users-summary []
  {:users (db/get-all-users-summary)})

(defn user-info [{{username :username} :identity}]
  #(db/get-user {:username username}))

(defn admin-page [section get-content-fn {{username :username} :identity}]
  (layout/render "member.html" {:username username
                                :admin true
                                :section section
                                :content (get-content-fn)}))

(defroutes admin-routes
  (GET "/admin" req (admin-page "overview" all-users-summary req))
  (GET "/admin/billing" req (admin-page "billing" all-users req))
  (GET "/admin/payment" req (admin-page "payment" all-users req))
  (GET "/admin/manage" req (admin-page "manage" (other-users req) req))
  (GET "/admin/settings" req (admin-page "settings" (user-info req) req))
  (POST "/admin/billing" req (do-transaction + req))
  (POST "/admin/payment" req (do-transaction - req))
  (POST "/admin/manage" req (do-manage req))
  (POST "/admin/settings" req (do-update-user req)))
