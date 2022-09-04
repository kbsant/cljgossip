(defproject cljgossip "0.1.0-SNAPSHOT"
  :description "MUD Gossip client library in clojure"
  :url "https://github.com/kbsant/cljgossip"
  :dependencies
  [[org.clojure/clojure "1.11.1"]
   [org.clojure/tools.logging "1.2.4"]
   [medley "1.4.0"]]
  :repl-options {:init-ns cljgossip.core})
