(defproject euro-program-app "0.1.0-SNAPSHOT"
  :description "euro-online.org conference program app"
  :url "https://www.euro-online.org/conf/"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  
  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [org.clojure/core.async  "0.4.490"]
                 [reagent "0.8.1"]
                 [reagent-forms "0.5.43"]
                 [reagent-utils "0.3.3"]
                 [clj-commons/secretary "1.2.4"] 
                 [domina "1.0.3"]
                 [org.clojure/tools.reader "1.3.2"]
                 [cljs-ajax "0.8.0"]]

  :plugins [[lein-figwheel "0.5.18"]
            [lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]]

  :source-paths ["src"]

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src"]
                :figwheel {:on-jsload "euro-program-app.core/on-js-reload"
                           :open-urls ["http://localhost:3449/index.html"]}

                :compiler {:main euro-program-app.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/euro_program_app.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}}
               {:id "min"
                :source-paths ["src"]
                :compiler {:output-to "resources/public/js/compiled/euro_program_app.js"
                           :main euro-program-app.core
                           :optimizations :advanced
                           :pretty-print false}}
               {:id "worker"
                :source-paths ["src-svc"]
                :compiler {:output-to "resources/public/service-worker.js"
                           :output-dir "resources/public/js/worker/out"
                           :main euro-program-app.service-worker
                           :optimizations :advanced
                           :pretty-print false}}]} 

  :figwheel {:css-dirs ["resources/public/css"]
             :nrepl-port 7888}


  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.10"]
                                  [figwheel-sidecar "0.5.18"]
                                  [cider/piggieback "0.4.1"]]
                   :source-paths ["src" "dev"]
                   :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}
                   :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                                     :target-path]}})
