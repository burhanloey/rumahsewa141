(ns rumahsewa141.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [rumahsewa141.layout :refer [error-page]]
            [rumahsewa141.routes.home :refer [home-routes]]
            [rumahsewa141.routes.register :refer [register-routes]]
            [rumahsewa141.routes.auth :refer [auth-routes]]
            [rumahsewa141.routes.member :refer [member-routes]]
            [rumahsewa141.routes.admin :refer [admin-routes]]
            [compojure.route :as route]
            [rumahsewa141.env :refer [defaults]]
            [mount.core :as mount]
            [rumahsewa141.middleware :as middleware]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    (-> #'home-routes
        ;; (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (-> #'register-routes
        ;; (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (-> #'auth-routes
        ;; (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (-> #'member-routes
        ;; (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats)
        (wrap-routes middleware/wrap-restricted))
    (-> #'admin-routes
        ;; (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats)
        (wrap-routes middleware/wrap-admin-only))    
    (route/not-found
     (:body
      (error-page {:status 404
                   :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
