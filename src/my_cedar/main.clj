(ns my-cedar.main
  (:require [clojure.java.io :as io]
            [my-cedar.core :as cedar]))

(def policies (slurp (io/resource "policies/documents.cedar")))

(def entities
  [{:uid (cedar/entity "Document" "hello")
    :attrs {:owner {:__entity (cedar/entity "User" "alice")}
            :locked false}
    :parents []}])

(defn request [user]
  {:principal (cedar/entity "User" user)
   :action (cedar/entity "Action" "read")
   :resource (cedar/entity "Document" "hello")})

(defn -main [& _]
  (doseq [user ["alice" "bob"]]
    (println user "=>" (name (:decision (cedar/authorize policies entities (request user)))))))
