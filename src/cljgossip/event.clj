;; Unaffilated, unofficial implementation of the gossip protocol: gossip.haus/docs
(ns cljgossip.event
  (:require
   [clojure.string :as string]))

(defn- with-opt-ref
  "Associate an optional ref field, if it is not blank."
  [ref m]
  (cond-> m
    (not (string/blank? ref)) (assoc "ref" ref)))

(defn authenticate
  "Data for an authentication request. Use when opening a websocket."
  [client-agent client-id client-secret]
  {"event" "authenticate"
   "payload" {"client_id" client-id
              "client_secret" client-secret
              "supports" ["channels"]
              "channels" ["gossip" "test"]
              "user_agent" client-agent}})

(defn heartbeat
  "Data for a heartbeat event."
  [players]
  (cond-> {"event" "heartbeat"}
    (seq players) (assoc "payload" players)))

(defn subscribe
  "Data to subscribe to channel. Use when responding to an authentication event."
  [ref channel-name]
  (with-opt-ref
    ref
    {"event" "channels/subscribe"
     "payload" {"channel" channel-name}}))

(defn unsubscribe
  "Data to unsubscribe from a channel."
  [ref channel-name]
  (with-opt-ref
    ref
    {"event" "channels/unsubscribe"
     "payload" {"channel" channel-name}}))

(defn send-all
  "Data to send send to all subcribers of a particular channel"
  [ref channel-name from-player-name message-text]
  (with-opt-ref
    ref
    {"event" "channels/send"
     "payload" {"channel" channel-name
                "name" from-player-name
                "message" message-text}}))

(defn sign-in
  "Data to sign in a player, so they may receive tells."
  [ref player-name]
  (with-opt-ref
    ref
    {"event" "players/sign-in"
     "payload" {"name" player-name}}))

(defn sign-out
  "Data to sign out player and no longer receive tells."
  [ref player-name]
  (with-opt-ref
    ref
    {"event" "players/sign-out"
     "payload" {"name" player-name}}))

(defn status-all
  "Data to request status of all."
  [ref]
  {"event" "players/status"
   "ref" ref})

(defn status-game
  "Data to request status of players belonging to a game."
  [ref game-name]
  {"event" "games/status"
   "ref" ref
   "payload" {"game" game-name}})

(defn tell-send
  "Data to send a tell. A ref is returned and can be used to dispatch the sending status."
  [ref from-name to-game to-name message-text utc-timestamp]
  {"event" "tells/send"
   "ref" ref
   "payload" {"from_name" from-name
              "to_game" to-game
              "to_name" to-name
              "sent_at" utc-timestamp
              "message" message-text}})

(def event-map
  {"authenticate" :cljgossip/on-authenticate-status
   "heartbeat" :cljgossip/on-heartbeat
   ;; No need to do anything special at the network level for a restart.
   ;; Just notify play1ers that the gossip server is restarting. #botmessage
   "restart" :cljgossip/on-restart
   ;; Receive an ack/nack after subsribing
   "channels/subscribe" :cljgossip/on-channels-subscribe-status
   ;; Receive an ack/nack after unsubscribing
   "channels/unsubscribe" :cljgossip/on-channels-unsubscribe-status
   ;; Receive notification a player has signed in.
   ;; Useful for creating a menu of tell recipients.
   "players/sign-in" :cljgossip/on-players-sign-in
   ;; Receive notication a player has signed out.
   ;; Useful for creating a menu of tell recipients.
   "players/sign-out" :cljgossip/on-players-sign-out
   ;; Receive status of connected players. Might want to filter by ref.
   "players/status" :cljgossip/on-players-status
   ;; Receive an ack whether a tell has been sent or not.
   "tells/send" :cljgossip/on-tell-status
   ;; Receive a tell
   "tells/receive" :cljgossip/on-tell-receive
   ;; Receive notifcation that a game has connected
   "games/connect" :cljgossip/on-game-connected
   ;; Receive notification that a game has disconnected
   "games/disconnect" :cljgossip/on-game-disconnected
   ;; Receive the status of a game.
   "games/status" :cljgossip/on-game-status})

(defn dispatch [handlers ws-client ev]
  (let [handler-fn (->> (get ev "event")
                        (get event-map)
                        (get handlers))]
    (when handler-fn
      (handler-fn ws-client ev))))

