(ns rumahsewa141.routes.auth
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [clojure.java.io :as io]
            [buddy.hashers :as hashers]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [rumahsewa141.db.core :as db]))

(defn login-page [{identity :identity}]
  (if (nil? identity)
    (layout/render "login.html")
    (redirect "/member")))

(defn lookup-user [username password]
  (if-let [user (db/get-user {:username username})]
    (if (hashers/check password (get user :password))
      (dissoc user :password))))

(defn do-login! [{{:keys [username password session] :as params} :params}]
  (if (b/valid? params
                {:username v/required
                 :password v/required})

    ;; If parameters are valid, lookup for the user in database,
    (if-let [{admin :admin :as user} (lookup-user username password)]
      (-> (redirect (if (true? admin) "/admin" "/member"))
          (assoc :session (assoc session :identity user)))
      "Wrong username or password.")

    ;; else, display errors.
    (str (first (b/validate params
                            {:username v/required
                             :password v/required})))))

(defn do-logout! [{session :session}]
  (-> (redirect "/")
      (assoc :session (dissoc session :identity))))

(defroutes auth-routes
  (GET "/login" req (login-page req))
  (POST "/login" req (do-login! req))
  (POST "/logout" req (do-logout! req)))
