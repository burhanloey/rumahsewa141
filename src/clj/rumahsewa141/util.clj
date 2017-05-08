(ns rumahsewa141.util)

(defn do-to-selected
  "Do the f function to all selected users."
  [users f]
  (doall (map f (flatten (vector users)))))

(defn parse-int
  "Parse string s to integer."
  [s]
  (Integer/parseInt s))
