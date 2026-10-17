(ns my-cedar.test-runner
  (:require [clojure.test :as test]
            [my-cedar.core-test]))

(defn -main [& _]
  (let [{:keys [fail error]} (test/run-tests 'my-cedar.core-test)]
    (shutdown-agents)
    (System/exit (if (zero? (+ fail error)) 0 1))))
