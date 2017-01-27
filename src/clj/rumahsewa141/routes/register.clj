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

(defn do-register! [{{:keys [username password
                             confirm nickname
                             phone_no] :as params} :params}]
  (if (b/valid? params
                :username [v/required available-username]
                :password v/required
                :confirm v/required)

    ;; If params is valid then register,
    (if (= password confirm)
      (when-let [_ (db/create-user!
                    (merge default-value
                           {:username username
                            :password (hashers/encrypt password)
                            :nickname nickname
                            :phone_no phone_no}))]
        (layout/render "success.html" {:title "Success!"
                                       :description
                                       "You have been registered."}))
      (layout/render "error_message.html" {:description
                                           "Wrong password confirmation."}))

    ;; else, display errors.
    (layout/render
     "error_message.html"
     {:title "Registration failed!"
      :description (apply str
                          (:username
                           (first
                            (b/validate
                             params
                             :username [v/required available-username]
                             :password v/required
                             :confirm v/required))))})))

(defroutes register-routes
  (GET "/register" [] (layout/render "register.html"
                                     {:allowed (:value
                                                (db/get-registration-config))}))
  (POST "/register" req (do-register! req)))
    
