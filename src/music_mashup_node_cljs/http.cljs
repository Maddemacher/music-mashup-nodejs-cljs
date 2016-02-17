(ns music-mashup-node-cljs.http
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require  [cljs.nodejs :as node]
             [cljs.core.async :refer [<! chan close!]]))

(def client (node/require "superagent"))

(defn get-response-body [response]
   (js->clj (.-body response) :keywordize-keys true))

(defn get [url]
  (let [channel (chan)]
    (-> (.get client url)
        (.end (fn [err res] (go (>! channel (get-response-body res))
                                (close! channel)))))
    channel))
