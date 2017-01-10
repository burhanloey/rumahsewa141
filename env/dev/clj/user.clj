(ns user
  (:require [mount.core :as mount]
            rumahsewa141.core))

(defn start []
  (mount/start-without #'rumahsewa141.core/http-server
                       #'rumahsewa141.core/repl-server))

(defn stop []
  (mount/stop-except #'rumahsewa141.core/http-server
                     #'rumahsewa141.core/repl-server))

(defn restart []
  (stop)
  (start))


