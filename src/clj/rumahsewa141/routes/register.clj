(ns rumahsewa141.routes.register
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [clojure.java.io :as io]
            [rumahsewa141.db.core :refer [create-user! get-user]]
            [buddy.hashers :as hashers]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [rumahsewa141.validators :refer [available-username
                                             optional-email]]))

(defn register-page []
  (layout/render "register.html"))

(defn do-register! [{{:keys [username password email] :as params} :params}]
  (if (b/valid? params
                :username [v/required available-username]
                :password v/required
                :email optional-email)

    ;; If registration is valid,
    (when-let [_ (create-user!
                  {:username username
                   :password (hashers/encrypt password)
                   :email email})]
      "You have been registered.")

    ;; else, display errors.
    (str (first (b/validate params
                            :username [v/required available-username]
                            :password v/required
                            :email optional-email)))))

(defroutes register-routes
  (GET "/register" [] (register-page))
  (POST "/register" req (do-register! req)))
