(ns cljgossip.core-test
  (:require [clojure.test :refer :all]
            [cljgossip.core :refer :all]
            [cljgossip.handlers :as handlers]))

(comment

  (def app
    (atom
     {:game
      {:players #{}}}))

  (def env nil) ;; replace nil with config map
  
  (defn list-players-fn [app]
    (fn []
      (get-in @app [:game :players])))

  (defn gossip-handlers [app]
    {:cljgossip/on-heartbeat
     (partial handlers/default-on-heartbeat (list-players-fn app))})

  (def conn (connect env (gossip-handlers app)))

  (sign-in conn "Frida")

  (swap! app update-in [:game :players] conj "Frida")
   
  (send-all conn "gossip" "Frida" "hi")
  
  (sign-in conn "Thor")

  (swap! app update-in [:game :players] conj "Thor")

  (send-all conn "gossip" "Thor" "hello")

  (send-to conn "Frida" "Game Name" "Thor" "hi, Thor")

  (status conn nil)

  (close conn)
 )
