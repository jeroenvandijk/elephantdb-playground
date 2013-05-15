(ns data.core
  (:refer-clojure :exclude [list])
  (:require [data.serialization :as s])
  (:use [cascalog.api]
        [elephantdb.cascalog.keyval :only [keyval-tap]])
  (:gen-class))

(defn serialize-key [^String k]
  (.getBytes k "UTF-8"))

(import 'java.nio.charset.Charset)
(defn deserialize-key [^bytes k]
  (String. k (Charset/forName "UTF-8")))

(defn edb-tap [& [path]]
  (let [path (or path "../data/domains/example")]
    (keyval-tap path :spec { :num-shards 32 
                             :coordinator "elephantdb.persistence.JavaBerkDB" 
                             :shard-scheme "elephantdb.partition.HashModScheme" })))
  

;; Generates simple key-value pairs. Note that both keys and values need to be serialized 
;; as bytes. Byte arrays is the only format elephantdb supports for values thus you need to
;; use something like Thrift to work with different types of data in a flexible way

(defmain generate [& args]
  (?- (edb-tap)
      (map 
        (fn [[k v]]  [(serialize-key k) (s/serialize v)])
        (vec {"a" 1 "b" 2 "c" 3 "d" 4 "e" 5 "f" 6})
        )))

(defmain list [& args]
  (let [data (edb-tap)]
    (?<- (stdout)
         [?key ?value]
         (data ?serialized-key ?serialized-value)
         (deserialize-key ?serialized-key :> ?key)
         (s/deserialize-count ?serialized-value :> ?value))))
  
(defmain list_raw [path]
  (?- (stdout) (edb-tap path)))