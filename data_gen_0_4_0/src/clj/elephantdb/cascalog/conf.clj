(ns elephantdb.cascalog.conf
  (:require [cascalog.workflow :as w])
  (:import [elephantdb DomainSpec DomainSpec$Args]
           [elephantdb.cascading NewElephantDBTap$Args]
           [java.util ArrayList HashMap]))

(defn convert-clj-domain-spec
  [{:keys [coordinator shard-scheme num-shards persistence-options]}]
  {:pre [(and coordinator shard-scheme num-shards)]}
  (if persistence-options
    (let [args (DomainSpec$Args.)]
      (set! (.persistenceOptions args) (HashMap. persistence-options))
      (DomainSpec. coordinator shard-scheme num-shards args))
    (DomainSpec. coordinator shard-scheme num-shards)))

(defn convert-args
  [{:keys [tmp-dirs source-fields
           timeout-ms version]}]
  (let [mk-list (fn [xs] (when xs (ArrayList. xs)))
        ret      (NewElephantDBTap$Args.)]
    (when source-fields
      (set! (.sourceFields ret) (w/fields source-fields)))
    (set! (.tmpDirs ret) (mk-list tmp-dirs))
    (when timeout-ms
      (set! (.timeoutMs ret) timeout-ms))
    (set! (.version ret) version)
    ret))