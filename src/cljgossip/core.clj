(ns cljgossip.core
  (:require
   [cljgossip.http-client.core :as client]
   [cljgossip.event :as event]
   [cljgossip.handlers :as handlers]
   [medley.core :as medley])
  (:import
   java.time.Instant))

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

(defn send-all
  "Send a message to all subscribers."
  [{:cljgossip/keys [ws-client]} source msg]
  (client/send
   ws-client
   (event/send-all
    "gossip"
    source
    msg)))

(defn send-to
  "Send a tell to a specific target. Returns ref uuid of the sent message."
  [{:cljgossip/keys [ws-client]} source target msg]
  (let [ref (medley/random-uuid)
        tstamp (.toString (Instant/now))]
    (client/send
     ws-client
     (event/tell-send
      "gossip"
      source
      target
      msg
      tstamp))
    ref))

(defn close [{:cljgossip/keys [ws-client]}]
  (client/close ws-client))

