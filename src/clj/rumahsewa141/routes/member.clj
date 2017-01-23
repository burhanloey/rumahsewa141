(ns rumahsewa141.routes.member
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [rumahsewa141.db.core :as db]))

(defn do-update-user [{{id :id} :identity {:keys [nickname phone_no]} :params}]
  (if-let [_ (db/update-user!
              {:id id
               :nickname nickname
               :phone_no phone_no})]
    (layout/render "success.html")))

(defn user-bills [{{id :id} :identity}]
  #(db/get-user-bills {:user_id id}))

(defn user-info [{{username :username} :identity}]
  #(db/get-user {:username username}))

(defn member-page [section get-content-fn {{:keys [username admin]} :identity}]
  (if (true? admin)
    (redirect "/admin")
    (layout/render "member.html" {:username username
                                  :section section
                                  :content (get-content-fn)})))

(defroutes member-routes
  (GET "/member" req (member-page "overview" (user-bills req) req))
  (GET "/member/settings" req (member-page "settings" (user-info req) req))
  (POST "/member/settings" req (do-update-user req)))
