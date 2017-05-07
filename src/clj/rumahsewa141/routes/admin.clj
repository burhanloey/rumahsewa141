(ns rumahsewa141.routes.admin
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [rumahsewa141.db.core :as db]
            [rumahsewa141.routes.member :refer [user-info]]
            [rumahsewa141.repository.user :refer [all-users
                                                  other-users
                                                  all-users-summary]]
            [rumahsewa141.repository.transaction :refer [transactions-count
                                                         latest-transactions]]
            [rumahsewa141.repository.config :refer [registration-allowed?]]
            [rumahsewa141.views :refer [history-view]]
            [rumahsewa141.math :refer [parse-double]]))

(defn- do-to-selected [users f]
  (doall (map f (flatten (vector users)))))

(defn update-registration-config [{{action :action} :params}]
  (when-let [_ (case action
               "allow" (db/allow-registration)
               "close" (db/close-registration))]
    (redirect "/admin/settings/registration")))

(defn- add-transaction [users sign rent internet others]
  (when-let [_ (do-to-selected users #(db/create-transaction!
                                     {:user_id (Integer/parseInt %)
                                      :rent (sign rent)
                                      :internet (sign internet)
                                      :others (sign others)}))]
    (layout/render "success.html" {:title "Done!"
                                   :description (if (pos? (sign 1))
                                                  "Selected users billed successfully."
                                                  "Payment received.")})))

(defn do-transaction [sign {{:keys [users rent-raw internet-raw others-raw]} :params}]
  (let [rent     (parse-double rent-raw)
        internet (parse-double internet-raw)
        others   (parse-double others-raw)]
    (cond
      (nil? users) (layout/render "error_message.html" {:description "Please select a user."})
      (zero? (+ rent internet others)) (layout/render "error_message.html" {:description "No point if no money involved."})
      :else (add-transaction users sign rent internet others))))

(defn- manage-users [users action]
  (when-let [_ (do-to-selected users (if (= action "delete")
                                       #(db/delete-user!
                                         {:id (Integer/parseInt %)})
                                       #(db/update-user-status!
                                         {:id (Integer/parseInt %)
                                          :admin (case action
                                                   "assign" true
                                                   "revoke" false)})))]
    (redirect "/admin/manage")))

(defn do-manage [{{:keys [users action]} :params}]
  (if (nil? users)
    (layout/render "error_message.html" {:description "Please select a user."})
    (manage-users users action)))

(defn admin-page [section
                  get-content-fn
                  {{username :username} :identity}
                  & [subsection]]
  (layout/render "member.html" (merge {:username username
                                       :admin true
                                       :section section
                                       :subsection subsection}
                                      (if (nil? get-content-fn)
                                        nil
                                        (get-content-fn)))))

(defn settings-page [subsection req & [get-content-fn]]
  (admin-page "settings" get-content-fn req subsection))

(defroutes admin-routes
  (GET "/admin" req (admin-page "overview" all-users-summary req))
  (GET "/admin/billing" req (admin-page "billing" all-users req))
  (GET "/admin/payment" req (admin-page "payment" all-users req))
  (GET "/admin/manage" req (admin-page "manage" (other-users req) req))
  (GET ["/admin/history/:page" :page #"[1-9][0-9]*"] [page :as req]
       (admin-page "history"
                   (history-view (Long/parseLong page) transactions-count latest-transactions)
                   req))
  (GET "/admin/settings/profile" req (settings-page "profile" req (user-info req)))
  (GET "/admin/settings/account" req (settings-page "account" req))
  (GET "/admin/settings/registration" req (settings-page "registration" req registration-allowed?))
  (POST "/admin/billing" req (do-transaction + req))
  (POST "/admin/payment" req (do-transaction - req))
  (POST "/admin/manage" req (do-manage req))
  (POST "/admin/settings/registration" req (update-registration-config req)))
