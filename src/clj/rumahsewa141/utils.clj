(ns rumahsewa141.utils)

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

(defn assoc-fee-display [{:keys [rent_fee internet_fee
                                 other_fees description] :as fee}]
  (assoc fee
         :display (str description " (rent: " rent_fee ", internet: "
                       internet_fee ", others: " other_fees ")")))
