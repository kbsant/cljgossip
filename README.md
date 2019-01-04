# cljgossip

A MUD Gossip client library in clojure.

This is an unaffiliated, unofficial implementation of <https://gossip.haus/docs> .

## What is this for?

This is a library written in clojure that allows players on [MUD](https://en.wikipedia.org/wiki/MUD) games to chat with each other using the [Gossip](https://gossip.haus/docs) protocol.

# Status

Alpha. Authorization, heartbeats, and chats are working. Other callbacks need more tests and samples.

# Usage

## Account setup
To use the library, first set up a [Gossip](https://gossip.haus/docs) client account.

## Connecting to Gossip
Confirm that the account is working by loading the credentials in the repl, and running `connect`.  The connect command automatically logs in and replies to heartbeats, which can be verified by checking the logs.

    (require '[cljgossip.core :as gossip])
    
    (def env {:cljgossip/ws-uri "wss://gossip socket uri"
              :cljgossip/client-id "id from config"
              :cljgossip/client-hash "secret from config"
              :cljgossip/client-agent "Name Of the Client Game"})

     ;; login with the env, connection function and a handler map.
     ;; the library will automatically merge in a default heartbeat handler.
     ;; In this example, the handler map is empty. For a sample implementation,
     ;; see core_test.clj.
     (def conn (gossip/login env client/ws-connect {}))
     ;; => get authentication and heartbeat messages

## Signing in

When a player signs in, announce it to Gossip so that they may receive tells.

     (gossip/sign-in conn "Frida")

## Status of other players

Request the status to get a list of all players everywhere:

     (gossip/status conn nil)

Or request the player list of a specific game:

     (gossip/status conn "OtherGame")

## Sending messages

Messages can be broadcasted to a channel

     ;; send to the test channel.
     (gossip/send-all conn "test" "Frida" "hi test")
     
     ;; If the channel name is nil, use "gossip" by default.
     ;; The list of channel names is available on the gossip.haus/docs site.
     (gossip/send-all conn nil "Frida" "hi all")

Or sent to a specific user:

     (gossip/send-to conn "Frida" "SampleValhallaGame" "Thor" "hi there, Thor")

## Receiving messages

All messages are logged by default using clojure.logging. The log level may be changed by setting the level of the namespace in log4j.properties.

To handle messages in an application, pass a map of callback functions when connecting:

    ;; define handler function
    (defn tell-cb [{:cljgossip/keys [ws-client]} ev]
      (log/info "got a tell: " ev)
      ;; process the tell event
      )
    
    ;; specify it in the callback map
    (def app-conn (gossip/connect env {:cljgossip/on-tell-receive tell-cb}))

## Signing out

Announce when a player signs out:

     (gossip/sign-out conn "Frida")

## Disconnecting

Gossip auto-disconnects after three heartbeats. To initiate the disconnection, use `close`.

     ;; disconnect.
     (gossip/close conn)


See:
* `cljgossip.handlers/default-on-heartbeat` for a sample handler
* `test/cljgossip/core_test.clj` for a sample session implementing a heartbeat that sends the player list
* `(vals cljgossip.events/event-map)` in `event.clj` for a list of callback keys. 

# Roadmap

Plans for this library:

* Separate the websocket library. The websocket library (jetty-based) is currently included in the main artifact. It should be moved into an optional artifact instead.

* Sample app. The code in `core_test.clj` should be in its own sample app project.

