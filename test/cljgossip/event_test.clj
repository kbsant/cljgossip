(ns cljgossip.event-test
  (:require [clojure.test :refer :all]
            [cljgossip.event :refer :all]))

(deftest dispatch-event-test
  (testing "An event gets dispatched to a handler function."
    (let [on-tell-receive (constantly "received")
          handlers {:cljgossip/on-tell-receive on-tell-receive}
          tell-event {"event" "tells/receive" "payload" {}}]
      (is (= "received"
             (dispatch handlers {} tell-event))))))

(deftest optional-ref-test
  (testing "Sign in takes an optional ref."
    (testing "Verify it is used if provided."
      (is (= {"event" "players/sign-in"
              "ref" "unique-ref"
              "payload" {"name" "cool-name"}}
             (sign-in "unique-ref" "cool-name"))))
    (testing "Verify it is not set if nil or empty."
      (is (= {"event" "players/sign-in"
              "payload" {"name" "cool-name2"}}
             (sign-in "" "cool-name2")
             (sign-in nil "cool-name2"))))))
