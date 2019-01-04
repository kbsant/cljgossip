(ns cljgossip.core-test
  (:require [clojure.test :refer :all]
            [cljgossip.core :refer :all]
            [cljgossip.handlers :as handlers]))

(comment

  ;; TODO create a dummy ws connection with an atom to verify tests.
  (defn test-ws-connect [uri event-handlers])

  (def app
    (atom
     {:game {:players #{}}
      :env {}}))

  (def env nil) ;; replace nil with config map
  
  (defn list-players-fn [app]
    (fn []
      (get-in @app [:game :players])))

  (defn gossip-handlers [app]
    {:cljgossip/on-heartbeat
     (partial handlers/default-on-heartbeat (list-players-fn app))})

  (def conn (login (:env @app) client/ws-connect (gossip-handlers app)))

  (sign-in conn "Frida")

  (swap! app update-in [:game :players] conj "Frida")
   
  (send-all conn "gossip" "Frida" "hi")
  
  (sign-in conn "Thor")

  (swap! app update-in [:game :players] conj "Thor")

  (send-all conn "gossip" "Thor" "hello")

  (send-to conn "Frida" "GameName" "Thor" "hi, Thor")

  (status conn nil)

  (close conn)

 )
