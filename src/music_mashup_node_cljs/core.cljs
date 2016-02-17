(ns music-mashup-node-cljs.core
  (:require [cljs.nodejs :as node]
            [music-mashup-node-cljs.serve :as mbh]))

(node/enable-util-print!)

(def express (node/require "express"))

(defn say-hello! [req res]
  (.send res "Hello world!"))

(defn -main []
  (let [app (express)]
    (.get app "/" say-hello!)
    (.get app "/mbid" mbh/serve-mbid)
    (.listen app 3000 (fn []
                        (println "Server started on port 3000")))))

(set! *main-cli-fn* -main)
