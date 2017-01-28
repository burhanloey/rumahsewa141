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

(defn update-registration-config [{{action :action} :params}]
  (if-let [_ (case action
               "allow" (db/allow-registration)
               "close" (db/close-registration))]
    (redirect "/admin/settings/registration")))

(defn do-transaction [sign {{:keys [users rent internet others]} :params}]
  (let [pr (parse-double rent)
        pi (parse-double internet)
        po (parse-double others)]
    (cond
      (nil? users) (layout/render "error_message.html"
                                  {:description "Please select a user."})
    
      (and (zero? pr)
           (zero? pi)
           (zero? po)) (layout/render "error_message.html"
                                      {:description
                                       "No point if no money involved."})
    
      :else (if-let [_ (do-to-selected users
                                       #(db/create-transaction!
                                         {:user_id (Integer/parseInt %)
                                          :rent (sign pr)
                                          :internet (sign pi)
                                          :others (sign po)}))]
              (layout/render "success.html"
                             {:title "Done!"
                              :description (if (pos? (sign 1))
                                             "Selected users billed successfully."
                                             "Payment received.")})))))

(defn do-manage [{{:keys [users action]} :params}]
  (if (nil? users)
    (layout/render "error_message.html" {:description "Please select a user."})
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

(defn admin-page [section get-content-fn
                  {{username :username} :identity} & [subsection]]
  (layout/render "member.html" (merge {:username username
                                       :admin true
                                       :section section
                                       :subsection subsection}
                                      (if (nil? get-content-fn)
                                        nil
                                        (get-content-fn)))))

(defn admin-page-test [section get-content-fn
                       {{username :username} :identity} & [subsection]]
  (layout/render "member.html" {:username username
                                :admin true
                                :section section
                                :subsection subsection}))

(defn settings-page [subsection req & [get-content-fn]]
  (admin-page "settings" get-content-fn req subsection))

(defn registration-allowed? []
  {:allowed (:value (db/get-registration-config))})

(defroutes admin-routes
  (GET "/admin" req (admin-page-test "overview" all-users-summary req))
  (GET "/admin/billing" req (admin-page "billing" all-users req))
  (GET "/admin/payment" req (admin-page "payment" all-users req))
  (GET "/admin/manage" req (admin-page "manage" (other-users req) req))
  (GET ["/admin/history/:page" :page #"[1-9][0-9]*"] [page :as req]
       (admin-page "history" (history-view (Long/parseLong page)
                                           transactions-count
                                           latest-transactions) req))
  (GET "/admin/settings/profile" req (settings-page "profile"
                                                    req
                                                    (user-info req)))
  (GET "/admin/settings/account" req (settings-page "account" req))
  (GET "/admin/settings/registration" req (settings-page "registration"
                                                         req
                                                         registration-allowed?))
  (POST "/admin/billing" req (do-transaction + req))
  (POST "/admin/payment" req (do-transaction - req))
  (POST "/admin/manage" req (do-manage req))
  (POST "/admin/settings/registration" req (update-registration-config req)))

