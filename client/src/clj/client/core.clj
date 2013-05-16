(ns client.core
  (:require [elephantdb.client :as c]
            [data.serialization :as s]
            [clojure.stacktrace]))

(def elephantdb-host (get (System/getenv) "ELEPHANTDB_HOST" (.getCanonicalHostName (java.net.InetAddress/getLocalHost))))

(defn query [callback]
  (c/with-elephant elephantdb-host 3578 conn (callback conn)))

(defprotocol ISerializeKey
  (serialize-key [k]))

(extend-protocol ISerializeKey
  Number
  (serialize-key [k] (.byteValue k))
  String
  (serialize-key [k] (.getBytes k "UTF-8")))

(def domains #(query c/get-domains))
(def status #(query c/get-status))
(def fully-loaded? #(query c/fully-loaded?))

(defn connected? []
  (try (status)
       true
       (catch org.apache.thrift.transport.TTransportException _ false)))

(defn shard-sets-unavailable?
  "Test if shards are available"[]
  (try
    ;; Trying fetching metadata about a domain. If it fails
    ;; something is wrong with the shards. Could be HOST over other setting.
    (query #(c/get-domain-metadata % (first (domains))))
    false
    (catch org.apache.thrift.transport.TTransportException _ true)))

(defn fetch-raw [domain ks]
  (try
    (query (fn [conn]
             (if (coll? ks)
               (c/multi-get conn domain (map serialize-key ks))
               (c/get conn domain (serialize-key ks)))))
    (catch org.apache.thrift.transport.TTransportException e
      (let [available-domains (set (domains))]
        (cond (not (available-domains domain))
              (println (str "The domain '" domain "' doesn't seem to exist. Choose one from the following: " (clojure.string/join ", " available-domains)))

              (shard-sets-unavailable?)
              (println (str "Could not retrieve metadata about the domains. Could be that a wrong host name has been used. Please check the server logs."))

              :else (clojure.stacktrace/print-stack-trace e))))
    (catch Exception e
      (clojure.stacktrace/print-stack-trace e))))

(defn fetch [domain ks]
  (let [rs (fetch-raw domain ks)]
    (if (coll? rs)
      (map s/deserialize rs)
      (s/deserialize rs))))

(defn fetch-instructions []
  (println "To see your domains run `(domains)`, currently we found the following:" (clojure.string/join ", " (domains)))
  (println)
  (println "To fetch a value `(fetch \"your-domain\" ks)`. `ks` can be one or more keys"))

(defn usage-instructions []
  (if (fully-loaded?)
    (do
      (if (shard-sets-unavailable?)
        (do (println (str "Could not retrieve metadata about the domains. Could be that a wrong host name has been used. Please check the server logs."))
            (println)
            (println "When you found the issue, please restart this repl so the correct settings are picked up."))
        (do
          (println "It appears your ElephantDB server is running properly.")
          (println)
          (fetch-instructions))))
    (do
      (println "It appears there not all domains are (completely) available. Are you sure you generated proper data?")
      (println)
      (println "For huge domains, it could take a while to load. Check the server logs in that case.")
      (println)
      (println "If you think the problem is solved run `(fetch-instructions)` here to continue")
      (println "Or, if you are not sure check again with run `(usage-instructions)` here to continue"))))

(defn server-instructions []
  (println "You are not connected to an ElephantDB server!")
  (println)
  (println "Start the server with `bin/repl` from the root of this project.")
  (println)
  (println "When the server is started run the command `(usage-instructions)` here to know what to do next."))

(defn repl-welcome-message
  "Prints everything needed for a new user to get started"
  []
  (println "Welcome to the ElephantDB repl!")
  (println)
  (when-not elephantdb-host
    (println "ELEPHANTDB_HOST env variable isn't set! Should probably be set to `localhost`.")
    (System/exit 1))
  (if (connected?)
    (usage-instructions)
    (server-instructions))
  (println))
