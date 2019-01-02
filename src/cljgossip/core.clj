(ns cljgossip.core
  (:require
   [cljgossip.http-client.core :as client]
   [cljgossip.event :as event]
   [cljgossip.handlers :as handlers]
   [medley.core :as medley])
  (:import
   java.time.Instant))

(defn str-uuid []
  (str (medley/random-uuid)))

(defn connect
  "Connect and login to a gossip server."
  ([{:keys [gossip-uri gossip-client-agent gossip-client-id gossip-client-hash]}
    gossip-handlers]
   (connect
    gossip-uri gossip-client-agent gossip-client-id gossip-client-hash gossip-handlers))
  ([gossip-uri gossip-client-agent gossip-client-id gossip-client-hash gossip-handlers]
   (let [conn (promise) ; need an ugly placeholder for the connection returned by the lib
         on-connect-promise (promise)
         ws-handlers (handlers/wrap-as-ws conn on-connect-promise gossip-handlers)
         client (client/connect gossip-uri ws-handlers)]
     (deliver conn {:cljgossip/ws-client client})
     (when (deref on-connect-promise 1000 nil)
       (handlers/authenticate-on-connect
        client gossip-client-agent gossip-client-id gossip-client-hash)
       @conn))))

(defn sign-in
  "Announce that a player signed in."
  [{:cljgossip/keys [ws-client]} player-name]
  (let [ref (str-uuid)]
    (client/send
     ws-client
     (event/sign-in ref player-name))
    ref))

(defn sign-out
  "Announce that a player signed out."
  [{:cljgossip/keys [ws-client]} player-name]
  (let [ref (str-uuid)]
    (client/send
     ws-client
     (event/sign-out ref player-name))
    ref))

(defn status
  "Request the status of a game, or all subscribers."
  [{:cljgossip/keys [ws-client]} game-name]
  (let [ref (str-uuid)]
    (client/send
     ws-client
     (if game-name
       (event/status-game ref game-name)
       (event/status-all ref)))
    ref))

(defn send-all
  "Send a message to all subscribers of a channel."
  [{:cljgossip/keys [ws-client]} channel-name source msg]
  (let [ref (str-uuid)]
    (client/send
     ws-client
     (event/send-all
      ref
      (or channel-name "gossip")
      source
      msg))))

(defn send-to
  "Send a tell to a specific target. Returns ref uuid of the sent message."
  [{:cljgossip/keys [ws-client]} source target-game target msg]
  (let [ref (str-uuid)
        tstamp (.toString (Instant/now))]
    (client/send
     ws-client
     (event/tell-send
      ref
      source
      target-game
      target
      msg
      tstamp))
    ref))

(defn close [{:cljgossip/keys [ws-client]}]
  (client/close ws-client))

