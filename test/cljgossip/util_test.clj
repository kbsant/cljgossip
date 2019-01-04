(ns cljgossip.util-test
  (:require [clojure.string :as string]
            [clojure.test :refer :all]
            [cljgossip.util :refer :all]))

;; Regex based tests may be better.
;; But sanity tests are sufficient because of the low complexity.
(deftest util-test
  (testing "str-uuid returns a non-blank string"
    (is (not (string/blank? (str-uuid)))))
  (testing "tstamp returns a non-blank string"
    (is (not (string/blank? (tstamp-utc-now))))))
