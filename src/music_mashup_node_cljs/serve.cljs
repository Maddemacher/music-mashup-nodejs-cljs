(ns music-mashup-node-cljs.serve
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.nodejs :as node]
            [goog.string :as s]
 	          [goog.string.format :as format]
            [cljs.core.async :refer [<! merge into chan close!]]
            [music-mashup-node-cljs.constants :as constants]
            [music-mashup-node-cljs.http :as http]
            [music-mashup-node-cljs.helpers :as h]))

(defn scrape-wiki [res]
  { :title (h/find-nested res :title) :description (h/find-nested res :extract)})

(defn get-wiki[wikidata]
  (http/get (s/format constants/wiki-base-url (re-find #"[^\/]+$" (:resource (:url wikidata))))))

(defn scrape-cover-art [response]
  { :image (:image (first (:images response)))})

(defn get-cover-art[id]
  (http/get (s/format constants/cover-art-base-url id)))

(defn scrape-music-brainz-response [res]
  { :wikipedia (some #(if (= "wikipedia" (:type %)) %) (:relations res))
    :albums    (filter #(= "Album" (:primary-type %)) (h/find-sveral-nested res :primary-type))})

(defn get-music-brainz [mbid]
  (http/get (s/format constants/music-brainz-base-url mbid)))

(defn search [mbid]
    (let [channel (chan)]
      (go (let [mb-response (scrape-music-brainz-response (<! (get-music-brainz mbid)))
                album-channels (into [] (merge (mapv #(get-cover-art (:id %)) (:albums mb-response))))
                wiki (scrape-wiki (<! (get-wiki (:wikipedia mb-response))))
                album-responses (<! album-channels)
                albums (map scrape-cover-art album-responses)]
            (>! channel { :mb mb-response :albums albums :wiki wiki })
            (close! channel)))
      channel))

(defn serve-mbid [req res]
  (go (let [search-response (<! (search (.-id (.-query req))))]
            (.send res (clj->js search-response)))))
