(ns cljgossip.emojiapi-test
  (:require [clojure.test :refer :all]
    [cljgossip.core :as core]
    [cljgossip.emojiapi :as emo]))

(deftest resolve-symbol-test
  (testing "Emojis resolve into the api functions."
    (is (= emo/🌈 core/login))
    (is (= emo/⭐️ core/send-all))
    (is (= emo/⚡️ core/send-to))
    (is (= emo/✨ core/sign-in))
    (is (= emo/❄️ core/sign-out))
    (is (= emo/🔭 core/status))
    (is (= emo/💔 core/close))))
