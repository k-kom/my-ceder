(ns my-cedar.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [my-cedar.core :as cedar]
            [my-cedar.main :as example]))

(deftest authorization-test
  (testing "Owner can read; other users are denied by default"
    (is (cedar/allowed? example/policies example/entities (example/request "alice")))
    (is (= {:decision :deny}
           (cedar/authorize example/policies example/entities (example/request "bob")))))
  (testing "Explicit forbid overrides permit"
    (is (false? (cedar/allowed? example/policies
                                (assoc-in example/entities [0 :attrs :locked] true)
                                (example/request "alice")))))
  (testing "No policies means deny"
    (is (false? (cedar/allowed? "" example/entities (example/request "alice")))))
  (testing "Context is passed to Cedar"
    (let [policy "permit(principal, action, resource) when { context.mfa };"
          request (example/request "alice")]
      (is (cedar/allowed? policy [] (assoc request :context {:mfa true})))
      (is (false? (cedar/allowed? policy [] (assoc request :context {:mfa false}))))))
  (testing "Invalid policies and evaluation errors are surfaced"
    (is (thrown? clojure.lang.ExceptionInfo
                 (cedar/authorize "not a policy" example/entities (example/request "alice"))))
    (is (thrown? clojure.lang.ExceptionInfo
                 (cedar/authorize example/policies
                                  (update-in example/entities [0 :attrs] dissoc :owner)
                                  (example/request "alice"))))))

(deftest input-test
  (is (thrown? clojure.lang.ExceptionInfo (cedar/entity "User" nil)))
  (is (thrown? clojure.lang.ExceptionInfo (cedar/authorize "" [] {})))
  (is (thrown? clojure.lang.ExceptionInfo
               (cedar/authorize "" [] (assoc (example/request "alice") :context nil))))
  (is (thrown? clojure.lang.ExceptionInfo
               (cedar/authorize "" [] (assoc (example/request "alice")
                                             :principal (cedar/entity "invalid type" "alice"))))))

(deftest binding-conversion-test
  (testing "Namespaced UIDs and escaped IDs pass through the Java binding"
    (is (cedar/allowed?
         "permit(principal, action, resource) when { principal == context.actor };"
         []
         (assoc (example/request "alice")
                :principal (cedar/entity "App::User" "alice\"\\\n")
                :context {:actor {:__entity (cedar/entity "App::User" "alice\"\\\n")}}))))
  (testing "Entity parents and nested context values survive conversion"
    (is (cedar/allowed?
         (str "permit(principal in Group::\"readers\", action, resource) "
              "when { context.session.level >= 2 && context.scopes.contains(\"read\") };")
         [{:uid (cedar/entity "User" "alice") :attrs {}
           :parents [(cedar/entity "Group" "readers")]}]
         (assoc (example/request "alice")
                :context {:session {:level 2} :scopes ["read" "write"]})))))

(deftest evaluation-errors-never-allow-test
  (testing "An allow from another policy does not hide an evaluation error"
    (is (thrown? clojure.lang.ExceptionInfo
                 (cedar/authorize
                  (str "permit(principal, action, resource);"
                       "forbid(principal, action, resource) when { context.missing };")
                  [] (example/request "alice"))))))

(deftest tutorial-authorization-test
  (let [results (example/tutorial-results)]
    (is (= (set (range 1 11)) (set (map :step results))))
    (doseq [{:keys [name decision expected]} results]
      (testing name (is (= expected decision))))))

(deftest tutorial-denial-test
  (doseq [{:keys [name policy entities request]} (example/tutorial-examples)]
    (testing (str name " rejects an action outside the policy scope")
      (is (false? (cedar/allowed?
                   (slurp (clojure.java.io/resource (str "tutorial/" policy)))
                   entities
                   (assoc request :action (cedar/entity "Action" "notAllowed"))))))))

(deftest tutorial-template-link-test
  (let [policy (example/tutorial-template)
        request {:principal (cedar/entity "User" "alice")
                 :action (cedar/entity "Action" "writeFile")
                 :resource (cedar/entity "Photo" "pic.jpg")}]
    (is (cedar/allowed? policy [] request))
    (is (false? (cedar/allowed? policy []
                                (assoc request :principal (cedar/entity "User" "bob")))))
    (is (false? (cedar/allowed? policy []
                                (assoc request :resource (cedar/entity "Photo" "other.jpg")))))))
