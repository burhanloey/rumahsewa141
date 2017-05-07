(ns rumahsewa141.routes.admin
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [rumahsewa141.repository.user :refer [all-users
                                                  other-users
                                                  all-users-summary
                                                  user-info
                                                  update-users]]
            [rumahsewa141.repository.transaction :refer [transactions-count
                                                         latest-transactions
                                                         create-transactions-for-users]]
            [rumahsewa141.repository.config :refer [registration-allowed?
                                                    update-registration-config]]
            [rumahsewa141.views :refer [history-view]]
            [rumahsewa141.math :refer [parse-double]]))

(defn update-registration [{{action :action} :params}]
  (when-let [_ (update-registration-config action)]
    (redirect "/admin/settings/registration")))

(defn- add-transaction [users sign rent internet others]
  (when-let [_ (create-transactions-for-users users sign rent internet others)]
    (layout/render "success.html" {:title "Done!"
                                   :description (if (pos? (sign 1))
                                                  "Selected users billed successfully."
                                                  "Payment received.")})))

(defn do-transaction [sign
                      {{users           :users
                        rent-string     :rent
                        internet-string :internet
                        others-string   :others  } :params}]
  (let [rent     (parse-double rent-string)
        internet (parse-double internet-string)
        others   (parse-double others-string)]
    (cond
      (nil? users) (layout/render "error_message.html" {:description "Please select a user."})
      (zero? (+ rent internet others)) (layout/render "error_message.html" {:description "No point if no money involved."})
      :else (add-transaction users sign rent internet others))))

(defn- manage-users [users action]
  (when-let [_ (update-users users action)]
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
                   (history-view (Long/parseLong page) transactions-count (latest-transactions req))
                   req))
  (GET "/admin/settings/profile" req (settings-page "profile" req (user-info req)))
  (GET "/admin/settings/account" req (settings-page "account" req))
  (GET "/admin/settings/registration" req (settings-page "registration" req registration-allowed?))
  (POST "/admin/billing" req (do-transaction + req))
  (POST "/admin/payment" req (do-transaction - req))
  (POST "/admin/manage" req (do-manage req))
  (POST "/admin/settings/registration" req (update-registration req)))
