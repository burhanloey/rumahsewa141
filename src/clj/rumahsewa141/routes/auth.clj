(ns rumahsewa141.routes.auth
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [clojure.java.io :as io]
            [buddy.hashers :as hashers]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [rumahsewa141.db.core :as db]
            [rumahsewa141.repository.user :refer [lookup-user]]))

(defn show-login-page [{identity :identity}]
  (if (nil? identity)
    (layout/render "login.html")
    (redirect "/member")))

(defn- valid-login? [params]
  (b/valid? params
            {:username v/required
             :password v/required}))

(defn- get-login-error [params]
  (str (first (b/validate params
                          {:username v/required
                           :password v/required}))))

(defn log-user-in [{{:keys [username password session] :as params} :params}]
  (if (valid-login? params)
    (if-let [{admin :admin :as user} (lookup-user username password)]
      (-> (redirect (if (true? admin) "/admin" "/member"))
          (assoc :session (assoc session :identity user)))
      (layout/render "error_message.html" {:title "Failed login"
                                           :description "Wrong username or password."}))
    (layout/render "error_message.html" {:description (get-login-error params)})))

(defn log-user-out [{session :session}]
  (-> (redirect "/")
      (assoc :session (dissoc session :identity))))

(defroutes auth-routes
  (GET "/login" req (show-login-page req))
  (POST "/login" req (log-user-in req))
  (POST "/logout" req (log-user-out req)))
