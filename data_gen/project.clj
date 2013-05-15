(defproject datagen "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :min-lein-version "2.0.0"
  
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :java-source-paths ["src/java"]
  
  :thrift-java-path "src/java"
  :thrift-source-path "src/thrift"
  
  :dependencies [[org.clojure/clojure "1.5.1"]
                 
                 [elephantdb/elephantdb-cascalog "0.4.4"]
                 [elephantdb/elephantdb-bdb "0.4.4"]

                 ;; FIXME Cascalog needs to have a version without elephantdb
                 [cascalog "1.10.1"  :exclusions [cascalog/cascalog-elephantdb ]]
                 
                 [org.apache.thrift/libthrift "0.8.0"]
                 
                 #_[elephantdb "0.2.0-wip4" :exclusions [org.slf4j/slf4j-api org.slf4j/slf4j-log4j12]]

                 [org.apache.hadoop/hadoop-core "0.20.2-dev"]
                 ]
                 
 :profiles {
   :dev {
     :plugins [
                [lein-thrift "0.1.2"]
              ]

 }})
