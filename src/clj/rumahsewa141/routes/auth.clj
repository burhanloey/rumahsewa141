(ns rumahsewa141.routes.auth
  (:require [rumahsewa141.layout :as layout]
            [rumahsewa141.services.user :as user]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [clojure.java.io :as io]
            [buddy.hashers :as hashers]
            [bouncer.core :as b]
            [bouncer.validators :as v]))

(defn show-login-page
  "Login page controller. If user logged in, redirect to member page,
  else, render login page."
  [{identity :identity}]
  (if (nil? identity)
    (layout/render "login.html")
    (redirect "/member")))

(defn- valid-login?
  "Check if login parameters are valid."
  [params]
  (b/valid? params
            {:username v/required
             :password v/required}))

(defn- get-login-error
  "Get login error message from the validation."
  [params]
  (str (first (b/validate params
                          {:username v/required
                           :password v/required}))))

(defn log-user-in
  "Login controller. When login credentials are correct, redirect
  according to user status, i.e, admin will be redirected to admin
  page, and normal user will be redirected to member page."
  [{{:keys [username password session] :as params} :params}]
  (if (valid-login? params)
    (if-let [{admin :admin :as user} (user/lookup-user username password)]
      (-> (redirect (if (true? admin) "/admin" "/member"))
          (assoc :session (assoc session :identity user)))
      (layout/render "error_message.html" {:title "Failed login"
                                           :description "Wrong username or password."}))
    (layout/render "error_message.html" {:description (get-login-error params)})))

(defn log-user-out
  "Logout controller. Redirect to homepage when successful."
  [{session :session}]
  (-> (redirect "/")
      (assoc :session (dissoc session :identity))))

(defroutes auth-routes
  (GET "/login" req (show-login-page req))
  (POST "/login" req (log-user-in req))
  (POST "/logout" req (log-user-out req)))
