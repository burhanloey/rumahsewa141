(ns rumahsewa141.routes.member
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [rumahsewa141.db.core :refer [get-users
                                          get-user-bills]]))

(defn assoc-total-bills-left [{:keys [rent_bill rent_payment
                                      internet_bill internet_payment
                                      other_bills other_payments]
                               :as user}]
  (assoc user
         :rent_bill_left     (- rent_bill
                                rent_payment)
         :internet_bill_left (- internet_bill
                                internet_payment)
         :other_bills_left   (- other_bills
                                other_payments)))

(defn admin-view []
  (let [users (get-users)]
    {:users (map assoc-total-bills-left users)}))

(defn member-page [{{id :id username :username admin :admin} :identity}]
  (let [user-bills (get-user-bills {:id id})]
    (layout/render "member.html" (merge (assoc-total-bills-left user-bills)
                                        {:username username}
                                        (when (true? admin)
                                          (assoc (admin-view) :admin admin))))))

(defroutes member-routes
  (GET "/member" req (member-page req)))
