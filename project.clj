(defproject org.clojars.civa86/thumbnailz "1.0.0"
  :description "Thumbnail generator library for Clojure"
  :url "https://github.com/civa86/thumbnailz"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [
                 [org.clojure/clojure "1.8.0"]
                 [net.mikera/imagez "0.11.0"]
                 [org.imgscalr/imgscalr-lib "4.2"]
                 ]
  :profiles {:dev {:plugins [[com.jakemccrary/lein-test-refresh "0.12.0"]]}}
  )
