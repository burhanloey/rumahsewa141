(ns rumahsewa141.routes.register
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [clojure.java.io :as io]
            [rumahsewa141.db.core :as db]
            [buddy.hashers :as hashers]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [rumahsewa141.validators :refer [available-username]]))

(def default-value {:nickname ""
                    :phone_no ""})

(defn do-register! [{{:keys [username password nickname phone_no] :as params} :params}]
  (if (b/valid? params
                :username [v/required available-username]
                :password v/required)

    ;; If params is valid then register,
    (when-let [_ (db/create-user!
                  (merge default-value
                         {:username username
                          :password (hashers/encrypt password)
                          :nickname nickname
                          :phone_no phone_no}))]
      "You have been registered.")

    ;; else, display errors.
    (str (first (b/validate params
                            :username [v/required available-username]
                            :password v/required)))))

(defroutes register-routes
  (GET "/register" [] (layout/render "register.html"))
  (POST "/register" req (do-register! req)))
