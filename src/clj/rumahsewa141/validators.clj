(ns rumahsewa141.validators
  (:require [bouncer.validators :as v]
            [rumahsewa141.db.core :as db]))

(v/defvalidator username-exist
  {:default-message-format "%s does not exist."}
  [value]
  (db/fetch-user-by-username {:username value}))

(v/defvalidator available-username
  {:default-message-format "%s has been taken."}
  [value]
  (not (username-exist value)))

(v/defvalidator optional-email
  {:default-message-format "%s must be a valid email address"}
  [value]
  (or (empty? value) (v/email value)))
