(ns rumahsewa141.routes.admin
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [rumahsewa141.db.core :as db]
            [rumahsewa141.routes.member :refer [user-info]]
            [rumahsewa141.views :refer [history-view]]
            [rumahsewa141.math :refer [parse-double]]))

(defn do-to-selected [users f]
  (doall (map f (flatten (vector users)))))

(defn do-transaction [sign {{:keys [users rent internet others]} :params}]
  (let [parsed-rent (parse-double rent)
        parsed-internet (parse-double internet)
        parsed-others (parse-double others)]
    (cond
      (nil? users) "Please select a user."
    
      (and (zero? parsed-rent)
           (zero? parsed-internet)
           (zero? parsed-others)) "No point if no money involved."
    
      :else (if-let [_ (do-to-selected users
                                       #(db/create-transaction!
                                         {:user_id (Integer/parseInt %)
                                          :rent (sign parsed-rent)
                                          :internet (sign parsed-internet)
                                          :others (sign parsed-others)}))]
              (redirect "/admin")))))

(defn do-manage [{{:keys [users action]} :params}]
  (if (nil? users)
    "Please select a user."
    (if-let [_ (do-to-selected users (if (= action "delete")
                                       #(db/delete-user!
                                         {:id (Integer/parseInt %)})
                                       #(db/update-user-status!
                                         {:id (Integer/parseInt %)
                                          :admin (case action
                                                   "assign" true
                                                   "revoke" false)})))]
      (redirect "/admin/manage"))))

(defn all-users []
  {:users (db/get-all-users)})

(defn other-users [{{id :id} :identity}]
  (fn [] {:users (db/get-other-users {:id id})}))

(defn all-users-summary []
  (let [users-summary (db/get-all-users-summary)
        index         (iterate inc 1)]
    {:users (map #(assoc %1 :index %2) users-summary index)}))

(defn transactions-count []
  (db/get-transactions-count))

(defn latest-transactions [params]
  (db/get-latest-transactions params))

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
  (GET ["/admin/history/:page" :page #"[1-9][0-9]*"] [page :as req]
       (admin-page "history" (history-view (Long/parseLong page)
                                           transactions-count
                                           latest-transactions) req))
  (GET "/admin/settings" req (admin-page "settings" (user-info req) req))
  (POST "/admin/billing" req (do-transaction + req))
  (POST "/admin/payment" req (do-transaction - req))
  (POST "/admin/manage" req (do-manage req)))
