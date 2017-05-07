(ns rumahsewa141.util)

(defn do-to-selected [users f]
  (doall (map f (flatten (vector users)))))
