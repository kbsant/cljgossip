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
              "supports" ["channels" "test"]
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
  {"authenticate" :on-authenticate-status
   "heartbeat" :on-heartbeat
   "restart" :on-restart
   "channels/subscribe" :on-channels-subscribe-status
   "channels/unsubscribe" :on-channels-unsubscribe-status
   "players/sign-in" :on-players-sign-in
   "players/sign-out" :on-players-sign-out
   "players/status" :on-players-status
   "tells/send" :on-tell-status
   "tells/receive" :on-tell-receive
   "games/connect" :on-game-connected
   "games/disconnect" :on-game-disconnected
   "games/status" :on-game-status})

(defn dispatch-event [handlers ev]
  (let [event-type (get event-map (get ev "event"))
        handler-fn (get handlers event-type)]
    (when handler-fn
      (handler-fn ev))))

(defmulti dispatch (fn [ev]) (get ev  "event"))

(defmethod dispatch :default [ev])

(defmethod dispatch "authenticate" [ev]
  )

(defmethod dispatch "heartbeat" [ev])

;; No need to do anything special at the network level for a restart.
;; Just notify play1ers that the gossip server is restarting. #botmessage
(defmethod dispatch "restart" [ev])

;; Receive an ack/nack after subsribing
(defmethod dispatch "channels/subscribe" [ev])

;; Receive an ack/nack after unsubscribing
(defmethod dispatch "channels/unsubscribe" [ev])

;; Receive notification a player has signed in.
;; Useful for creating a menu of tell recipients.
(defmethod dispatch "players/sign-in" [ev])

;; Receive notication a player has signed out.
;; Useful for creating a menu of tell recipients.
(defmethod dispatch "players/sign-out" [ev])

;; Receive status of connected players.
(defmethod dispatch "players/status" [ev])

;; Receive an ack whether a tell has been sent or not.
(defmethod dispatch "tells/send" [ev])

;; Receive a tell
(defmethod dispatch "tells/receive" [ev])

;; Receive notifcation that a game has connected
(defmethod dispatch "games/connect" [ev])

;; Receive notification that a game has disconnected
(defmethod dispatch "games/disconnect" [ev])

;; Receive the status of a game.
(defmethod dispatch "games/status" [ev])

