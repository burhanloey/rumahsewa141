(ns rumahsewa141.routes.member
  (:require [rumahsewa141.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [rumahsewa141.db.core :refer [get-membership-fees
                                          get-total-payments]]))

(defn member-page [{{id :id username :username} :identity}]
  (let [{:keys [rental_fee internet_bill other_bills]} (get-membership-fees {:user_id id})
        {:keys [rent_payment internet_payment other_payment]} (get-total-payments {:user_id id})]
    (layout/render "member.html" {:name          username
                                  :rental_fee    (- rental_fee rent_payment)
                                  :internet_bill (- internet_bill internet_payment)
                                  :other_bills   (- other_bills other_payment)})))

(defn admin-page [req]
  (layout/render "member.html" {:name ""
                                :rental_fee ""
                                :internet_bill ""
                                :other_bills ""}))

(defn show-page [{{admin :admin} :identity :as req}]
  (if (nil? admin)
    (member-page req)
    (admin-page req)))

(defroutes member-routes
  (GET "/member" req (show-page req)))
