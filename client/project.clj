(defproject client "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
            
  ;; Necessary because we depend on the classes in data_gen. It would be
  ;; better if there was a shared lib with the deserialization functionality
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]

  :repl-options { :init-ns client.core
                  :init (do (in-ns 'client.core) (repl-welcome-message)) }
            
  :dependencies [
                 [org.clojure/clojure "1.5.1"]
                 [elephantdb/elephantdb-client "0.4.4"]])
