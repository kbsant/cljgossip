(ns cljgossip.http-client.core
  (:require
   [gniazdo.core :as ws]))

(defn connect [uri]
  (ws/connect uri))
