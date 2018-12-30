(ns cljgossip.core
  (:require
   [cljgossip.http-client.core :as client]
   [cljgossip.event :as event]))

(defn send [env source msg]
  ())

(defn send-to [env source target msg])


(defn chat-handler
  [{:keys [gossip-client-agent gossip-client-id gossip-client-hash]}
   ws-client]
  {:on-connect
   (fn [session]
     (client/post
      ws-client
      (event/authenticate
       gossip-client-agent
       gossip-client-id
       gossip-client-hash)))
   :on-receive
   (fn [message]
     (event/dispatch (read-json message)))
   :on-error
   (fn [ex])
   :on-close
   (fn [status-code reason])})

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


