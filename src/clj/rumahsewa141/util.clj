(ns rumahsewa141.util)

(defn do-to-selected [users f]
  (doall (map f (flatten (vector users)))))

(defn parse-int [s]
  (Integer/parseInt s))
