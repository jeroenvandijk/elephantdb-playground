(defproject datagen "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :min-lein-version "2.0.0"
  
  
  :jvm-opts ["-XX:MaxPermSize=128M"
             "-XX:+UseConcMarkSweepGC"
             "-Xms1024M" "-Xmx1048M" "-server"
             ]

  
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :java-source-paths ["src/java"]
  
  :thrift-java-path "src/java"
  :thrift-source-path "src/thrift"
  
  :repl-options { :init (repl-welcome-message) }
  
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [cascalog-elephantdb "0.3.2"]

                 ;; We cannot use Cascalog 1.10, see https://gist.github.com/a13313c6f089a839a0de#comments
                 [cascalog "1.9.0"]
                 
                 [org.apache.thrift/libthrift "0.7.0"]
                 
                 [elephantdb "0.2.0-wip4" :exclusions [org.slf4j/slf4j-api org.slf4j/slf4j-log4j12]]

                 [org.apache.hadoop/hadoop-core "0.20.2-dev"]
                 ]
                 
 :profiles {
   :dev {
     :plugins [
                [lein-thrift "0.1.2"]
              ]

 }})
