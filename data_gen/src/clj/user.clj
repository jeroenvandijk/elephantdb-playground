(ns user
  (:require [elephantdb.keyval.core :as kc]
            [data.serialization :as s]
            [clojure.stacktrace]))

(def elephantdb-ip (System/getenv "ELEPHANTDB_IP"))

(defn query [callback]
  (kc/with-kv-connection elephantdb-ip 3578 conn (callback conn)))

(defmulti getter (fn [_ _ ks] 
  (if (coll? ks) 
    [:multi (class (first ks))]
    [:single (class ks)])))

(defmethod getter [:single Integer] [conn domain ks]
  (.getInt conn domain ks))
(defmethod getter [:multi Integer] [conn domain ks]
  (.multiGetInt conn domain ks))
(defmethod getter [:single Long] [conn domain ks]
  (.getLong conn domain ks))
(defmethod getter [:multi Long] [conn domain ks]
  (.multiGetLong conn domain ks))
(defmethod getter [:single String] [conn domain ks]
  (.getString conn domain ks))
(defmethod getter [:multi String] [conn domain ks]
  (.multiGetString conn domain ks))

(defn fetch-raw [domain ks]
  (try
    (query #(getter % domain ks))
    (catch org.apache.thrift7.transport.TTransportException e
      (println "Are you sure the domain exists?:")
      (clojure.stacktrace/print-stack-trace e))
    (catch Exception e
      (clojure.stacktrace/print-stack-trace e))
  ))
  
(defn fetch [domain ks]
  (let [rs (fetch-raw domain ks)]
    (if (coll? rs)
      (map s/deserialize rs)
      (s/deserialize rs))))

(defn domains []
  (query #(.getDomains %)))
  
(defn status []
  (query #(.getStatus %)))

(defn repl-welcome-message
  "Prints everything needed for a new user to get started"
  []
  (println)
  (println "Welcome to Elephantdb repl")
  (println)
  (println "To see your domains `(domains)`")
  (println "To fetch a value `(fetch \"your-domain\" ks)` ks can be one or more keys")
  (println)
  (println "Note: Before playing in the repl make sure you have a local server running (`bin/serve`)\nand that it has succesfully loaded shards (see log output and look for 'Copied)'")
  (println))
