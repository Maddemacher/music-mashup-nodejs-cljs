(ns music-mashup-node-cljs.constants)

(def music-brainz-base-url "http://musicbrainz.org/ws/2/artist/%s?&fmt=json&inc=url-rels+release-groups")

(def wiki-base-url "https://en.wikipedia.org/w/api.php?rawcontinue=true&action=query&format=json&prop=extracts&titles=%s")

(def cover-art-base-url "http://coverartarchive.org/release-group/%s")
