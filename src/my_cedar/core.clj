(ns my-cedar.core
  "Clojure API for the in-process cedar-java JNI binding."
  (:require [clojure.data.json :as json])
  (:import [com.cedarpolicy BasicAuthorizationEngine CedarJson]
           [com.cedarpolicy.model AuthorizationRequest AuthorizationResponse
            AuthorizationSuccessResponse AuthorizationResponse$SuccessOrFailure]
           [com.cedarpolicy.model.entity Entities]
           [com.cedarpolicy.model.policy PolicySet]
           [com.cedarpolicy.value EntityTypeName EntityUID Value]
           [java.util Map]))

(defn entity
  "Create an entity UID map for a request or an entity record."
  [type id]
  (when-not (and (string? type) (seq type) (string? id) (seq id))
    (throw (ex-info "Entity type and id must be non-empty strings" {})))
  {:type type :id id})

(defn- java-uid ^EntityUID [{:keys [type id]}]
  (entity type id)
  (let [parsed (EntityTypeName/parse type)]
    (when-not (.isPresent parsed)
      (throw (ex-info "Invalid Cedar entity type" {:type type})))
    (.of ^EntityTypeName (.get parsed) ^String id)))

(def ^:private engine (delay (BasicAuthorizationEngine.)))

(defn- java-context ^Map [context]
  ;; Cedar's own deserializer handles nested records, sets and entity escapes.
  (.readValue (CedarJson/objectReader) ^String (json/write-str context) Value))

(defn authorize
  "Evaluate Cedar policy text, entity maps and a principal/action/resource request.
  Optional :context is a map. Returns {:decision :allow|:deny}.
  Parsing and evaluation failures throw ExceptionInfo; JNI loading errors propagate."
  [policies entities request]
  (when-not (and (string? policies) (sequential? entities)
                 (map? request) (every? #(contains? request %) [:principal :action :resource])
                 (map? (get request :context {})))
    (throw (ex-info "Expected policy text, entity sequence and principal/action/resource request with map context" {})))
  (try
    (let [policy-set (PolicySet/parsePolicies policies)
          entity-set (Entities/parse (json/write-str entities))
          auth-request (AuthorizationRequest.
                        (java-uid (:principal request))
                        (java-uid (:action request))
                        (java-uid (:resource request))
                        (java-context (get request :context {})))
          ^AuthorizationResponse response
          (.isAuthorized ^BasicAuthorizationEngine @engine auth-request policy-set entity-set)]
      (when-not (= AuthorizationResponse$SuccessOrFailure/Success (.-type response))
        (throw (ex-info "Cedar authorization failed"
                        {:errors (mapv str (.orElse (.-errors response) []))})))
      (let [^AuthorizationSuccessResponse result (.get (.-success response))
            errors (.getErrors result)]
        (when (seq errors)
          (throw (ex-info "Cedar policy evaluation failed" {:errors (mapv str errors)})))
        {:decision (if (.isAllowed result) :allow :deny)}))
    (catch clojure.lang.ExceptionInfo e
      (throw e))
    (catch Exception e
      (throw (ex-info "Cedar evaluation failed" {} e)))))

(defn allowed?
  "Return true only for an explicit allow. Engine failures propagate."
  [policies entities request]
  (= :allow (:decision (authorize policies entities request))))
