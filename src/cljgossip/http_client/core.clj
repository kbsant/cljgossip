;; A default websocket wrapper for gossip.
;; If you want to use a different webocket library, write a connect function
;; that takes a URI and handler map, and returns a client map (see ws-connect)
(ns cljgossip.http-client.core
  (:require
   [clojure.data.json :as json]
   [gniazdo.core :as ws]
   [medley.core :as medley]))

(defn- native-handlers
  "Map handler to websocket implementation, converting messages from json to edn.
  The handler keys are specific to the ws library (in this case, gniazdo)"
  [{:cljgossip/keys [ws-on-connect ws-on-receive ws-on-error ws-on-close]}]
  {:on-connect ws-on-connect
   :on-receive (fn [msg] (ws-on-receive (json/read-str msg)))
   :on-error ws-on-error
   :on-close ws-on-close})

(defn- close [client]
  (ws/close client))

;; TODO -- only tested sync. Need to check the behavior of an async implementation.
(defn- send-as-json
  "Send a message to the socket. The message will be converted to json."
  [client message]
  (ws/send-msg client (json/write-str message)))

(defn- ws-client
  "Given a connection, return a map of websocket methods."
  [ws-conn]
  {:cljgossip/ws-conn ws-conn
   :cljgossip/ws-send (partial send-as-json ws-conn)
   :cljgossip/ws-close (partial close ws-conn)})

(defn ws-connect
  "Given a URI and gossip handlers, create a connection
  and return a map of webssocket methods."
  [uri ws-handlers]
  (when-let [conn (medley/mapply ws/connect uri (native-handlers ws-handlers))]
    (ws-client conn)))

