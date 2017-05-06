(ns rumahsewa141.math)

(defn abs "(abs n) is the absolute value of n" [n]
  (cond
    (not (number? n)) (throw (IllegalArgumentException.
                              "abs requires a number"))
    (neg? n) (- n)
    :else n))

(defn parse-double [num]
  ;; if num string match decimal regex with a precision of 2
  (if (re-find #"^[0-9]+(\.[0-9]{1,2})?$" num)
    (Double/parseDouble num)
    0.00))
