(ns rumahsewa141.util)

(defn do-to-selected
  "Do the f function to all selected users."
  [users f]
  (doall (map f (flatten (vector users)))))

(defn parse-int
  "Parse string to integer."
  [s]
  (Integer/parseInt s))

(defn parse-double
  "Check if num string matches decimal regex with a precision of 2.
  If match, parse to double, else return 0.00."
  [num]
  (if (re-find #"^[0-9]+(\.[0-9]{1,2})?$" num)
    (Double/parseDouble num)
    0.00))
