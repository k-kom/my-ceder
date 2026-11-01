(ns my-cedar.main
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [my-cedar.core :as cedar])
  (:import [com.cedarpolicy.model.policy Policy PolicySet TemplateLink LinkValue]
           [com.cedarpolicy.value EntityUID]))

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

(defn tutorial-examples
  "Load the numbered tutorial requests from resources."
  []
  (json/read-str (slurp (io/resource "tutorial/requests.json")) :key-fn keyword))

(defn- tutorial-policy [path]
  (slurp (io/resource (str "tutorial/" path))))

(defn tutorial-template
  "Link the step 10 template to Alice and pic.jpg through cedar-java."
  []
  (PolicySet.
   #{}
   #{(Policy. (tutorial-policy "10-03-template.cedar") "photo-sharing")}
   [(TemplateLink.
     "photo-sharing" "alice-photo-sharing"
     [(LinkValue. "?principal" (.get (EntityUID/parse "User::\"alice\"")))
      (LinkValue. "?resource" (.get (EntityUID/parse "Photo::\"pic.jpg\"")))])]))

(defn tutorial-results
  "Authorize every tutorial example, including the linked step 10 template."
  []
  (let [examples (tutorial-examples)
        linked (assoc (last examples) :name "10-03-template-linked"
                      :policy (tutorial-template))]
    (mapv (fn [{:keys [step name policy entities request expected]}]
            (assoc (cedar/authorize (if (string? policy)
                                     (tutorial-policy policy)
                                     policy)
                                   entities request)
                   :step step :name name :expected (keyword expected)))
          (conj examples linked))))

(defn -main [& _]
  (doseq [user ["alice" "bob"]]
    (println user
             "=>"
             (name (:decision (cedar/authorize policies
                                               entities
                                               (request user))))))
  (doseq [{:keys [name decision]} (tutorial-results)]
    (println name "=>" (clojure.core/name decision))))
