(defproject music-mashup-node-cljs "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :min-lein-version "2.5.3"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [org.clojure/core.async "0.2.374"]]

  :plugins [[lein-cljsbuild "1.1.2"]
            [lein-figwheel "0.5.0-4"]
            [lein-npm "0.6.1"]]

  :source-paths ["src"]

  :clean-targets ["server.js"
                  "target"]

    :npm {:dependencies  [[express "4.11.1"]
                          [superagent "1.7.2"]]}

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :figwheel true
              :compiler {
                :main music-mashup-node-cljs.core
                :output-to "target/server_dev/music_mashup_node_cljs.js"
                :output-dir "target/server_dev"
                :target :nodejs
                :optimizations :none
                :source-map true}}
             {:id "prod"
              :source-paths ["src"]
              :compiler {
                :output-to "target/server_prod/server.js"
                :output-dir "target/server_prod"
                :target :nodejs
                :optimizations :simple}}]})
