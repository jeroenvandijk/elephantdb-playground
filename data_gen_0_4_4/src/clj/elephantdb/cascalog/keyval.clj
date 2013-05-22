(ns elephantdb.cascalog.keyval
  (:use cascalog.api)
  (:require [cascalog.workflow :as w]
            [elephantdb.cascalog.core :as core])
  (:import [cascalog.ops IdentityBuffer]
           [org.apache.hadoop.conf Configuration]
           [elephantdb Utils]
           [elephantdb.partition ShardingScheme]
           [org.apache.hadoop.io BytesWritable]))

(defn- test-array
  [t]
  (let [check (type (t []))]
    (fn [arg] (instance? check arg))))

(def ^{:private true} byte-array?
  (test-array byte-array))

(defmapop [shard [^ShardingScheme scheme shard-count]]
  "Returns the shard to which the supplied shard-key should be
  routed."
  [^bytes shard-key]
  {:pre [(byte-array? shard-key)]}
  (.shardIndex scheme shard-key shard-count))

(defmapop mk-sortable-key [^bytes shard-key]
  {:pre [(byte-array? shard-key)]}
  (BytesWritable. shard-key))

(defn elephant<-
  [elephant-tap kv-src]
  (let [spec        (.getSpec elephant-tap)
        scheme      (.getShardScheme spec)
        shard-count (.getNumShards spec)]
    (<- [!shard !key !value]
        (kv-src !keyraw !valueraw)
        (shard [scheme shard-count] !keyraw :> !shard)
        (mk-sortable-key !keyraw :> !sort-key)
        (:sort !sort-key)
        ((IdentityBuffer.) !keyraw !valueraw :> !key !value))))

(defn keyval-tap
  "Returns a tap that can be used to source and sink key-value pairs
  to ElephantDB."
  [root-path & {:as args}]
  (let [args (merge {:source-fields ["key" "value"]}
                    args
                    {:sink-fn elephant<-})]
    (apply core/elephant-tap
           root-path
           (apply concat args))))
