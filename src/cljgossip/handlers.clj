(ns cljgossip.handlers
  (:require
   [cljgossip.http-client.core :as client]
   [cljgossip.event :as event]
   [clojure.tools.logging :as log]))

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

(defn default-heartbeat-handler [{:cljgossip/keys [ws-client]} ev]
  (log/info "reply to heartbeat: " ev)
  (client/post-as-json
   ws-client
   (event/heartbeat nil)))

(def default-gossip-handlers
  {:cljgossip/on-heartbeat default-heartbeat-handler})

(defn authenticate-on-connect
  [ws-client gossip-client-agent gossip-client-id gossip-client-hash]
  (client/post-as-json
   ws-client
   (event/authenticate
    gossip-client-agent
    gossip-client-id
    gossip-client-hash)))

(defn wrap
  "Wrap gossip handlers as socket handlers"
  [conn on-connect-promise gossip-handlers]
  (client/handlers
   {:cljgossip/ws-on-connect
    (fn [session]
      (deliver on-connect-promise session))
    :cljgossip/ws-on-receive
    (fn [message]
      (log/info "ws on receive:" message)
      (event/dispatch (merge default-gossip-handlers gossip-handlers) @conn message))
    :cljgossip/ws-on-error
    (fn [ex] (log/info "ws on error: " ex))
    :cljgossip/ws-on-close
    (fn [status-code reason] (log/info "status: " status-code "reason: " reason))}))

