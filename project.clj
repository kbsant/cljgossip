(defproject cljgossip "0.1.0-SNAPSHOT"
  :description "MUD Gossip client library in clojure"
  :url "https://github.com/kbsant/cljgossip"
  :dependencies
  [[org.clojure/clojure "1.10.0"]
   [org.clojure/tools.logging "0.4.1"]
   [medley "1.0.0"]]
  :repl-options {:init-ns cljgossip.core})
