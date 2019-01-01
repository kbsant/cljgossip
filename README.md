# cljgossip

A MUD Gossip client library in clojure.

This is an unaffiliated, unofficial implementation of <https://gossip.haus/docs> .

## What is this for?

This is a library written in clojure that allows players on [MUD](https://en.wikipedia.org/wiki/MUD) games to chat to each other using the [Gossip](https://gossip.haus/docs) protocol.

# Status

Early development. Authorization and heartbeats are tested and working. Other functions (tell, list, etc) are supported, but have no examples.

# Usage

To use the library, first set up a [Gossip](https://gossip.haus/docs) client account. Confirm that the account is working by loading the credentials in the repl, and running `connect`.  The connect command automatically logs in and replies to heartbeats, which can be verified by checking the logs.

    (require '[cljgossip.core :as gossip])
    
    (def env {:gossip-uri "wss://gossip socket uri"
              :gossip-client-id "id from config"
              :gossip-client-hash "secret from config"
              :gossip-client-agent "Name Of the Client Game"})

     ;; connect with the env and and empty handler map.
     ;; the library will automatically merge in a default heartbeat handler
     (def conn (gossip/connect env {}))
     ;; => get authentication and heartbeat messages
     ;; disconnect.
     (gossip/close conn)

# Roadmap

Plans for this library:

* Complete sample. Other functions can be handled (see `cljgossip.event` namespace), but a more complete sample is needed.

* Separate websocket library. The http library (jetty-based) is currently included in the main package. It should be moved into an optional package instead.



