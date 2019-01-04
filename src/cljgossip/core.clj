(ns cljgossip.core
  (:require
   [cljgossip.event :as event]
   [cljgossip.handlers :as handlers]
   [medley.core :as medley])
  (:import
   java.time.Instant))

(defn str-uuid []
  (str (medley/random-uuid)))

(defn login
  "Connect and login to a gossip server.
  This requires 3 arguments:
  * a configuration map - containing the uri, and authentication details,
  * a connection function - for calling the websocket implementation,
  * and a map of handlers - to process gossip events"
  ([{:cljgossip/keys [ws-uri client-agent client-id client-hash]}
    connect-fn
    event-handlers]
   (login
    ws-uri connect-fn client-agent client-id client-hash event-handlers))
  ([ws-uri connect-fn client-agent client-id client-hash event-handlers]
   (let [client (promise) ; need an ugly placeholder for the connection returned by the lib
         on-connect-promise (promise)
         ws-handlers (handlers/wrap-as-ws client on-connect-promise event-handlers)
         conn (connect-fn ws-uri ws-handlers)]
     (when conn
       (deliver client conn))
     (when (deref on-connect-promise 1000 nil)
       (handlers/authenticate conn client-agent client-id client-hash)
       conn))))

(defn sign-in
  "Announce that a player signed in."
  [{:cljgossip/keys [ws-send]} player-name]
  (let [ref (str-uuid)]
    (ws-send (event/sign-in ref player-name))
    ref))

(defn sign-out
  "Announce that a player signed out."
  [{:cljgossip/keys [ws-send]} player-name]
  (let [ref (str-uuid)]
    (ws-send (event/sign-out ref player-name))
    ref))

(defn status
  "Request the status of a game, or all subscribers."
  [{:cljgossip/keys [ws-send]} game-name]
  (let [ref (str-uuid)]
    (ws-send
     (if game-name
       (event/status-game ref game-name)
       (event/status-all ref)))
    ref))

(defn send-all
  "Send a message to all subscribers of a channel."
  [{:cljgossip/keys [ws-send]} channel-name source msg]
  (let [ref (str-uuid)]
    (ws-send
     (event/send-all
      ref
      (or channel-name "gossip")
      source
      msg))))

(defn send-to
  "Send a tell to a specific target. Returns ref uuid of the sent message."
  [{:cljgossip/keys [ws-send]} source target-game target msg]
  (let [ref (str-uuid)
        tstamp (.toString (Instant/now))]
    (ws-send (event/tell-send ref source target-game target msg tstamp))
    ref))

(defn close [{:cljgossip/keys [ws-close]}]
  (ws-close))

