(ns rumahsewa141.views
  (:require [rumahsewa141.math :refer [abs]]))

(defn find-verb
  "Return a string that explains the supplied arguments. If the
  arguments are positive, return 'was billed', else, return 'paid'."
  [a b c]
  (if (or (pos? a) (pos? b) (pos? c))
    "was billed"
    "paid"))

(defn describe-transaction
  "Describe transaction in words understandable by human."
  [{:keys [username rent internet others transaction_timestamp]}]
  {:description (apply str (drop-last (str username
                                           " "
                                           (find-verb rent internet others)
                                           (when-not (zero? rent)
                                             (str " RM " (abs rent) " for rent,"))
                                           (when-not (zero? internet)
                                             (str " RM " (abs internet) " for internet,"))
                                           (when-not (zero? others)
                                             (str "  RM " (abs others) " for other bills,")))))
   :timestamp transaction_timestamp})

(defn history-view
  "Return a function that will display transaction history with
  pagination."
  [page get-count-fn get-transactions-fn]
  #(let [max-items        10            ; max no of items displayed 
         prange           5             ; pagination range
         get-count-result (future (get-count-fn))
         transactions     (future (get-transactions-fn
                                   {:max_items max-items
                                    :offset (* (dec page) max-items)}))
         first-page       (-> page
                              dec
                              (quot prange)
                              (* prange)
                              inc)
         pages            (take prange (iterate inc first-page))
         {tcount :tcount} @get-count-result
         total-pages      (if (zero? (mod tcount max-items))
                            (quot tcount max-items)
                            (inc (quot tcount max-items)))
         has-page?        (fn [page] (<= page total-pages))
         available-pages  (take-while has-page? pages)]
     {:transactions  (map describe-transaction @transactions)
      :current_page  page
      :prev_page     (dec first-page)
      :pages         available-pages
      :next_page     (inc (last pages))
      :no_next_page  (or (< (count available-pages) prange)
                         (= (last pages) total-pages))}))
