(ns cljgossip.core
  (:require
   [cljgossip.http-client.core :as client]
   [cljgossip.event :as event]
   [cljgossip.handlers :as handlers]))

(defn connect
  "Connect and login to a gossip server."
  ([{:keys [gossip-uri gossip-client-agent gossip-client-id gossip-client-hash]}
    gossip-handlers]
   (connect
    gossip-uri gossip-client-agent gossip-client-id gossip-client-hash gossip-handlers))
  ([gossip-uri gossip-client-agent gossip-client-id gossip-client-hash gossip-handlers]
   (let [conn (atom {}) ; need an ugly placeholder for the connection returned by the lib
         on-connect-promise (promise)
         ws-handlers (handlers/wrap conn on-connect-promise gossip-handlers)
         client (client/connect gossip-uri ws-handlers)]
     (swap! conn assoc :cljgossip/ws-client client)
     (when (deref on-connect-promise 1000 nil)
       (handlers/authenticate-on-connect
        client gossip-client-agent gossip-client-id gossip-client-hash)
       @conn))))

(defn send-all [{:cljgossip/keys [ws-client]} source msg]
  (client/post-as-json
   ws-client
   (event/send-all
    "gossip"
    source
    msg)))

(defn send-to [{:cljgossip/keys [ws-client]} source target msg])

(defn close [{:cljgossip/keys [ws-client]}]
  (client/close ws-client))

