(ns data.core
  (:require [elephantdb.cascalog.keyval :as new])
  (:use [cascalog.api]
        [cascalog.elephantdb.keyval :only [keyval-tap]])
  (:gen-class))

(defn edb-tap [& [path]]
  (let [path (or path "../data/domains/example_0_2_0")]
    (keyval-tap path :spec { :num-shards 32 
                             :coordinator "elephantdb.persistence.JavaBerkDB" 
                             :shard-scheme "elephantdb.partition.HashModScheme" })))

(defmain migrate [& args]
  (?- (new/keyval-tap "../data/domains/example_0_2_0_to_0_4_4" :spec { :num-shards 32 
                           :coordinator "elephantdb.persistence.NewJavaBerkDB" 
                           :shard-scheme "elephantdb.partition.HashModScheme"})
      (<- [!key !value]
        ((edb-tap) !key !value))))
