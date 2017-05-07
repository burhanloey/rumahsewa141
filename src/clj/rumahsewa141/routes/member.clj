(ns rumahsewa141.routes.member
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [rumahsewa141.db.core :as db]
            [rumahsewa141.repository.user :refer [user-bills
                                                  user-info
                                                  wrong-password?]]
            [rumahsewa141.repository.transaction :refer [transactions-count
                                                         latest-transactions]]
            [rumahsewa141.views :refer [history-view]]
            [buddy.hashers :as hashers]))

(defn do-update-user [{{id :id}                    :identity
                       {:keys [nickname phone_no]} :params}]
  (when-let [_ (db/update-user! {:id id
                                 :nickname nickname
                                 :phone_no phone_no})]
    (layout/render "success.html" {:title "Done!"
                                   :description "You have updated your info."})))

(defn- update-password [id new]
  (when-let [_ (db/change-password! {:id id
                                     :password (hashers/encrypt new)})]
    (layout/render "success.html" {:title "Success!"
                                   :description "Password changed."})))

(defn change-password [{{:keys [id username]}     :identity
                        {:keys [old new confirm]} :params}]
  (cond
    (not= new confirm) (layout/render "error_message.html" {:description "Wrong password confirmation."})
    (wrong-password? username old) (layout/render "error_message.html" {:description "Wrong password."})
    :else (update-password id new)))

(defn member-page [section
                   get-content-fn
                   {{:keys [username admin]} :identity}
                   & [subsection]]
  (if (true? admin)
    (redirect "/admin")
    (layout/render "member.html" (merge {:username username
                                         :section section
                                         :subsection subsection}
                                        (if (nil? get-content-fn)
                                          nil
                                          (get-content-fn))))))

(defn settings-page [subsection req & [get-content-fn]]
  (member-page "settings" get-content-fn req subsection))

(defroutes member-routes
  (GET "/member" req (member-page "overview" (user-bills req) req))
  (GET ["/member/history/:page" :page #"[1-9][0-9]*"] [page :as req]
       (member-page "history"
                    (history-view (Long/parseLong page) (transactions-count req) (latest-transactions req))
                    req))
  (GET "/member/settings/profile" req (settings-page "profile" req (user-info req)))
  (GET "/member/settings/account" req (settings-page "account" req))
  (POST "/settings/profile" req (do-update-user req))
  (POST "/settings/account" req (change-password req)))
