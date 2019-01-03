(ns cljgossip.handlers
  (:require
   [cljgossip.http-client.core :as client]
   [cljgossip.event :as event]
   [clojure.tools.logging :as log]))

;; Simplify state machine:
;; * allow
;;   ** active: login, send, send-all, list-game, list-all, player-list, close
;;   ** reactive: hbeat, receive-bcast, receive-tell, player-list, disconnected
;; * details:
;;   ** login: composed of connect and auth.
;;      authenticate after connecting and receiving the socket client.
;;   ** gossip picks up the player list from the heartbeat message,
;;      so the app need to provide a player list callback.
;;      For richer functionality, e.g. qos indicator, handlers for on-heartbeat
;;      and on-restart may be implemented.
;;

(defn default-on-heartbeat [player-list-fn {:cljgossip/keys [ws-client]} ev]
  (log/info "reply to heartbeat: " ev)
  (client/send-as-json
   ws-client
   (event/heartbeat (player-list-fn))))

(def default-gossip-handlers
  {:cljgossip/on-heartbeat (partial default-on-heartbeat (constantly nil))})

(defn authenticate-on-connect
  [ws-client gossip-client-agent gossip-client-id gossip-client-hash]
  (client/send-as-json
   ws-client
   (event/authenticate
    gossip-client-agent
    gossip-client-id
    gossip-client-hash
    nil)))

(defn wrap-as-ws
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

