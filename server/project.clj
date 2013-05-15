(defproject elephantdb-local "0.1.0"
  :description "Elephantdb server for local development and debugging"
  :url "http://github.com/jeroenvandijk/elephantdb-dev"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :jvm-opts ["-XX:MaxPermSize=128M"
             "-XX:+UseConcMarkSweepGC"
             "-Xms1024M" "-Xmx1048M" "-server"]

  :dependencies [
                 [org.clojure/clojure "1.5.1"]
                 [elephantdb/elephantdb-server "0.4.4"]
                 [org.apache.hadoop/hadoop-core "0.20.2-dev" :exclusions [log4j commons-logging commons-codec org.slf4j/slf4j-api org.slf4j/slf4j-log4j12]]
                 
                 ;; Your libraries for deserialization...
                ])