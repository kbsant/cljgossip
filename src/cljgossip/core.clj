(ns cljgossip.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


(defn authenticate
  "Send authentication. Called by handler of websocket on-open."
  [client-id client-secret])

(defn heartbeat
  "Dispatched in response to a heartbeat event."
  [players])

(defn subscribe
  "Subscribe to channel. Dispatched in response to authenticate event."
  [ref channel-name])

(defn unsubscribe
  "Unsubscribe before closing the socket."
  [ref channel-name])

(defn send-all
  "Send to channel using channels/send"
  [channel-name player-name message-text])

(defn sign-in
  "Player signs in, and may receive tells."
  [player-name])

(defn sign-out
  "Player signs out and no longer receives tells."
  [player-name])

(defn status-all
  "Request status of all."
  [])

(defn status-game
  "Request status of players belonging to a game."
  [])

(defn tell-send
  "Send a tell. A ref is returned and can be used to dispatch the sending status."
  [from-name to-game to-name message-text])

(defmulti dispatch-event :event)

(defmethod dispatch-event "authenticate" [ev])

(defmethod dispatch-event "heartbeat" [ev])

;; No need to do anything special at the network level for a restart.
;; Just notify players that the gossip server is restarting. #botmessage
(defmethod dispatch-event "restart" [ev])

;; Receive an ack/nack after subsribing
(defmethod dispatch-event "channels/subscribe" [ev])

;; Receive an ack/nack after unsubscribing
(defmethod dispatch-event "channels/unsubscribe" [ev])

;; Receive notification a player has signed in.
;; Useful for creating a menu of tell recipients.
(defmethod dispatch-event "players/sign-in" [ev])

;; Receive notication a player has signed out.
;; Useful for creating a menu of tell recipients.
(defmethod dispatch-event "players/sign-out" [ev])

;; Receive status of connected players.
(defmethod dispatch-event "players/status" [ev])

;; Receive an ack whether a tell has been sent or not.
(defmethod dispatch-event "tells/send" [ev])

;; Receive a tell
(defmethod dispatch-event "tells/receive" [ev])

;; Receive notifcation that a game has connected
(defmethod dispatch-event "games/connect" [ev])

;; Receive notification that a game has disconnected
(defmethod dispatch-event "games/disconnect" [ev])

;; Receive the status of a game.
(defmethod dispatch-event "games/status" [ev])
