(ns util.core)

(defn local-hostname []
  (println (.getCanonicalHostName (java.net.InetAddress/getLocalHost))))
