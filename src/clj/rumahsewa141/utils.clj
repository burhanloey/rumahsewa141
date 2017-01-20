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
