(ns data.serialization
  (:import [org.apache.thrift TSerializer TDeserializer]
           [data.schema Value Count]))

(def serializer (TSerializer.))

(defprotocol ISerialize
  (serialize [v]))

(extend-type Value
  ISerialize
  (serialize [v] (.serialize serializer v)))

(defn- serialize-long [^Long v]
  (serialize (doto (Value.) (.setCount (Count. v)))))

(extend-type Integer
  ISerialize
  (serialize [v] (serialize-long (long v))))

(extend-type Long
  ISerialize
  (serialize [v] (serialize-long v)))

(def deserializer (TDeserializer.))

(defprotocol IDeserializeValue
  (deserialize-value [v]
    ))

(defprotocol IDeserialize
  (deserialize [v]))

;; Is there a better way to get the type of a byte[]
(extend-type (class (byte-array []))
  IDeserialize
  (deserialize [bytes]
    (let [v (Value.)]
      (.deserialize deserializer v bytes)
      v)))

(extend-type elephantdb.generated.Value
  IDeserialize
  (deserialize [v]
    (deserialize (.get_data v))))
    
(extend-type Value
  IDeserialize
  (deserialize [v] v))
  
(extend-type nil
  IDeserialize
  (deserialize [_] nil))
  
(defn deserialize-count [^Value v]
  (when-let [v (deserialize v)]
    (.. v getCount getValue)))
