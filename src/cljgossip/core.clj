(ns cljgossip.core
  (:require
   [cljgossip.http-client.core :as client]
   [cljgossip.event :as event]))

;; Simplify state machine:
;; * allow
;;   ** active: login, send, send-all, list-game, list-all, player-list, close
;;   ** reactive: receive-bcast, receive-tell, player-list, disconnected
;;   ** implied: hbeat
;; * details:
;;   ** login: composed of connect and auth.
;;      authenticate after connecting and receiving the socket client.
;;   ** client doesn't need to know about heartbeats. mostly. maybe just a qos indicator.
;;
(defn- wrap-handlers
  "Wrap gossip handlers as socket handlers"
  [pconn gossip-handlers]
  {:cljgossip/ws-on-connect
   (fn [session]
     (client/post-as-json
      @pconn
      (event/authenticate
       gossip-client-agent
       gossip-client-id
       gossip-client-hash)))
   :cljgossip/ws-on-receive
   (fn [message]
     (event/dispatch (read-json message)))
   :cljgossip/ws-on-error
   (fn [ex])
   :cljgossip/ws-on-close
   (fn [status-code reason])})

(defn connect
  "Connect and login to a gossip server."
  [gossip-uri gossip-client-agent gossip-client-id gossip-client-hash gossip-handlers]
  (let [pconn (atom nil) ; need an ugly placeholder for the connection returned by the lib
        ws-handlers (ws/handlers (wrap-handlers pconn gossip-handlers))
        client (ws/connect gossip-uri ws-handlers)]
    (reset! pconn client)
    {:cljgossip/ws-client client}))

(defn send-all [{:cljgossip/keys [ws-client]} source msg])

(defn send-to [{:cljgossip/keys [ws-client]} source target msg])

(defn close [{:cljgossip/keys [ws-client]}]
  (ws/close ws-client))

