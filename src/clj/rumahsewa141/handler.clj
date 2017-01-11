(ns rumahsewa141.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [rumahsewa141.layout :refer [error-page]]
            [rumahsewa141.routes.home :refer [home-routes]]
            [rumahsewa141.routes.restricted :refer [restricted-routes]]
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
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (-> #'restricted-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats)
        (wrap-routes middleware/wrap-restricted))
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
