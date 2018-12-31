(ns cljgossip.http-client.core
  (:require
   [clojure.data.json :as json]
   [gniazdo.core :as ws]
   [medley.core :as medley]))

(defn connect [uri handlers]
  (medley/mapply ws/connect uri handlers))

(defn close [client]
  (ws/close client))

(defn post-as-json
  "Post a message to the socket asynchronously."
  [client message]
  (ws/send-msg client (json/write-str message)))

(defn handlers
  "Map handler to websocket implementation, converting messages to edn"
  [{:cljgossip/keys [ws-on-connect ws-on-receive ws-on-error ws-on-close]}]
  {:on-connect ws-on-connect
   :on-receive (fn [msg] (ws-on-receive (json/read-str msg)))
   :on-error ws-on-error
   :on-close ws-on-close})
