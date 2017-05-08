(ns rumahsewa141.test.db.core
  (:require [rumahsewa141.db.core :refer [*db*] :as db]
            [luminus-migrations.core :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [rumahsewa141.config :refer [env]]
            [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'rumahsewa141.config/env
      #'rumahsewa141.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

(deftest test-users
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (is (= 1 (db/create-user!
               t-conn
               {:username "dingdong123"
                :nickname "Ding Dong"
                :phone_no "012-9876543"
                :password "pass"})))
    (is (= {:username "dingdong123"
            :nickname "Ding Dong"
            :phone_no "012-9876543"
            :password "pass"
            :admin    nil}
           (dissoc (db/fetch-user-by-username t-conn {:username "dingdong123"})
                   :id)))))
