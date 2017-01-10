(ns rumahsewa141.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[rumahsewa141 started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[rumahsewa141 has shut down successfully]=-"))
   :middleware identity})
