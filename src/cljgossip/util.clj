;; This namespace is for implementation only, not for public consumption.
(ns cljgossip.util
  (:require
   [medley.core :as medley])
  (:import
   java.time.Instant))

(defn str-uuid
  "Return a random uuid string."
  []
  (str (medley/random-uuid)))

(defn tstamp-utc-now
  "Return a timestamp in ISO8601 format and UTC timezone."
  []
  ;; java Instant/now is ISO and in UTC, according to spec.
  (.toString (Instant/now)))
