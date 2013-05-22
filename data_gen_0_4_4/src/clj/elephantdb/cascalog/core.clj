(ns elephantdb.cascalog.core
  (:use cascalog.api
        [elephantdb.cascalog conf])
  (:require [cascalog.workflow :as w])
  (:import [elephantdb Utils]
           [elephantdb.cascading NewElephantDBTap NewElephantDBTap$TapMode]
           [org.apache.hadoop.conf Configuration]))

(defn elephant-tap
  [root-path & {:keys [spec sink-fn ignore-spec] :as args}]
  (let [args (convert-args args)
        spec (when spec
               (convert-clj-domain-spec spec))
        ignore-spec (and (or spec false) (or ignore-spec false))
        source-tap (NewElephantDBTap. root-path spec args NewElephantDBTap$TapMode/SOURCE ignore-spec)
        sink-tap (NewElephantDBTap. root-path spec args NewElephantDBTap$TapMode/SINK ignore-spec)]
    (cascalog-tap source-tap
                  (if sink-fn
                    (fn [tuple-src]
                      [sink-tap (sink-fn sink-tap tuple-src)])
                    sink-tap))))