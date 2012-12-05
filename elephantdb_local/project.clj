(defproject elephantdb-local "0.1.0"
  :description "Elephantdb server for local development and debugging"
  :url "http://github.com/jeroenvandijk/elephantdb-dev"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :jvm-opts ["-XX:MaxPermSize=128M"
             "-XX:+UseConcMarkSweepGC"
             "-Xms1024M" "-Xmx1048M" "-server"]

  :dependencies [
                 [org.clojure/clojure "1.4.0"]
                 [elephantdb "0.2.0-wip4" :exclusions [commons-io]]
                 [org.apache.hadoop/hadoop-core "0.20.2-dev" :exclusions [log4j commons-logging commons-codec]]
                 
                 ;; Your libraries for deserialization...
                ])