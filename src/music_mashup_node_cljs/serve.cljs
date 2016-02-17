(ns music-mashup-node-cljs.serve
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.nodejs :as node]
            [clojure.string :as s]
            [cljs.core.async :refer [<! merge into chan close!]]
            [music-mashup-node-cljs.constants :as constants]
            [music-mashup-node-cljs.http :as http]
            [music-mashup-node-cljs.helpers :as h]))

(def util (node/require "util"))

(defn scrape-wiki [res]
  { :title (h/find-nested res :title) :description (h/find-nested res :extract)})

(defn get-wiki[wikidata]
  (http/get (.format util constants/wiki-base-url (last (s/split (:resource (:url wikidata)) "/")))))

(defn scrape-cover-art [response]
  { :image (:image (first (:images response)))})

(defn get-cover-art[id]
  (http/get (.format util constants/cover-art-base-url id)))

(defn scrape-music-brainz-response [res]
  { :wikipedia (some #(if (= "wikipedia" (:type %)) %) (:relations res))
    :albums    (filter #(= "Album" (:primary-type %)) (h/find-sveral-nested res :primary-type))})

(defn get-music-brainz [mbid]
  (http/get (.format util constants/music-brainz-base-url mbid)))

(defn search [mbid]
    (let [channel (chan)]
    (go (let [mb-response (scrape-music-brainz-response (<! (get-music-brainz mbid)))
              album-responses (<! (into [] (merge (mapv #(get-cover-art (:id %)) (:albums mb-response)))))
              albums (map scrape-cover-art album-responses)
              wiki (scrape-wiki (<! (get-wiki (:wikipedia mb-response))))]
          (>! channel { :mb mb-response :albums albums :wiki wiki })
          (close! channel)))
      channel))

(defn serve-mbid [req res]
  (go (let [search-response (<! (search (.-id (.-query req))))]
            (.send res (clj->js search-response)))))
