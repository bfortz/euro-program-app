(ns user
  (:require [figwheel-sidecar.repl-api]))

;; user is a namespace that the Clojure runtime looks for and
;; loads if its available

;; You can place helper functions in here. This is great for starting
;; and stopping your webserver and other development services

;; The definitions in here will be available if you run "lein repl" or launch a
;; Clojure repl some other way

;; You have to ensure that the libraries you :require are listed in your dependencies

;; Once you start down this path
;; you will probably want to look at
;; tools.namespace https://github.com/clojure/tools.namespace
;; and Component https://github.com/stuartsierra/component

(defn cljs-repl
  "Launch a ClojureScript REPL that is connected to your build and host environment."
  []
  (figwheel-sidecar.repl-api/repl-env "dev"))
