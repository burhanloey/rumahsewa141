(ns rumahsewa141.routes.admin
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [rumahsewa141.db.core :as db]
            [rumahsewa141.routes.member :refer [user-info]]))

(defn abs "(abs n) is the absolute value of n" [n]
  (cond
    (not (number? n)) (throw (IllegalArgumentException.
                              "abs requires a number"))
    (neg? n) (- n)
    :else n))

(defn parse-double [num]
  ;; if num string does not match decimal regex with a precision of 2
  (if (nil? (re-find #"^[0-9]+(\.[0-9]{1,2})?$" num))
    0.00
    (Double/parseDouble num)))

(defn do-to-selected [users f]
  (doall (map f (flatten (vector users)))))

(defn do-transaction [sign {{:keys [users rent internet others]} :params}]
  (let [parsed-rent (parse-double rent)
        parsed-internet (parse-double internet)
        parsed-others (parse-double others)]
    (cond
      (nil? users) "Please select a user."
    
      (and (zero? parsed-rent)
           (zero? parsed-internet)
           (zero? parsed-others)) "No point if no money involved."
    
      :else (if-let [_ (do-to-selected users
                                       #(db/create-transaction!
                                         {:user_id (Integer/parseInt %)
                                          :rent (sign parsed-rent)
                                          :internet (sign parsed-internet)
                                          :others (sign parsed-others)}))]
              (redirect "/admin")))))

(defn do-manage [{{:keys [users action]} :params}]
  (if (nil? users)
    "Please select a user."
    (if-let [_ (do-to-selected users (if (= action "delete")
                                       #(db/delete-user!
                                         {:id (Integer/parseInt %)})
                                       #(db/update-user-status!
                                         {:id (Integer/parseInt %)
                                          :admin (case action
                                                   "assign" true
                                                   "revoke" false)})))]
      (redirect "/admin/manage"))))

(defn all-users []
  {:users (db/get-all-users)})

(defn other-users [{{id :id} :identity}]
  (fn [] {:users (db/get-other-users {:id id})}))

(defn all-users-summary []
  (let [users-summary (db/get-all-users-summary)
        index         (iterate inc 1)]
    {:users (map #(assoc %1 :index %2) users-summary index)}))

(defn find-verb [a b c]
  (if (or (pos? a) (pos? b) (pos? c))
    "was billed"
    "paid"))

(defn describe-transaction [{:keys [username rent
                                    internet others transaction_timestamp]}]
  {:description (apply
                 str
                 (drop-last
                  (str username " " (find-verb rent internet others)
                       (when-not (zero? rent)
                         (str " RM " (abs rent) " for rent,"))
                       (when-not (zero? internet)
                         (str " RM " (abs internet) " for internet,"))
                       (when-not (zero? others)
                         (str "  RM " (abs others) " for other bills,")))))
   :timestamp transaction_timestamp})

(defn latest-transactions [page]
  #(let [max-items        10            ; max no of items displayed 
         prange           5             ; pagination range
         {tcount :tcount} (db/get-transactions-count)
         total-pages      (inc (quot tcount max-items)) 
         transactions     (db/get-latest-transactions
                           {:max_items max-items
                            :offset (* (dec page) max-items)})
         first-page       (inc (* (quot (dec page) prange) prange))
         pages            (take prange (iterate inc first-page))
         has-page?        (fn [page] (< page (inc total-pages)))
         available-pages  (take-while has-page? pages)]
     {:transactions  (map describe-transaction transactions)
      :current_page  page
      :prev_page     (dec first-page)
      :pages         available-pages
      :next_page     (inc (last pages))
      :no_next_page  (< (count available-pages) prange)}))

(defn admin-page [section get-content-fn {{username :username} :identity}]
  (layout/render "member.html" {:username username
                                :admin true
                                :section section
                                :content (get-content-fn)}))

(defroutes admin-routes
  (GET "/admin" req (admin-page "overview" all-users-summary req))
  (GET "/admin/billing" req (admin-page "billing" all-users req))
  (GET "/admin/payment" req (admin-page "payment" all-users req))
  (GET "/admin/manage" req (admin-page "manage" (other-users req) req))
  (GET ["/admin/history/:page" :page #"[1-9][0-9]*"] [page :as req]
       (admin-page "history" (latest-transactions (Long/parseLong page)) req))
  (GET "/admin/settings" req (admin-page "settings" (user-info req) req))
  (POST "/admin/billing" req (do-transaction + req))
  (POST "/admin/payment" req (do-transaction - req))
  (POST "/admin/manage" req (do-manage req)))
