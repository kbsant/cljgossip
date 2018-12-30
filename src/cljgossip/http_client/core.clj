(ns cljgossip.http-client.core
  (:require
   [gniazdo.core :as ws]))

(defn connect [uri]
  (ws/connect uri))

(defn post
  "Post a message to the socket asynchronously."
  [session message])
