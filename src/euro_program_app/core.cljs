(ns euro-program-app.core
  (:require [reagent.session :as s]
            [reagent.core :as r]
            [cljs.reader :as reader]
            [euro-program-app.display :as d]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [ajax.core :refer [GET]])
  (:require-macros [euro-program-app.macros :as m])
  (:import goog.History))

(enable-console-print!)

;; Routes

(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (s/put! :page :schedule))

(secretary/defroute "/schedule" []
  (s/put! :page :schedule))

(secretary/defroute "/timeslot/:id" [id]
  (s/put! :page :timeslot)
  (s/put! :timeslot (reader/read-string id)))

(secretary/defroute "/session/:id" [id]
  (s/put! :page :session)
  (s/put! :session (reader/read-string id)))

(secretary/defroute "/streams" []
  (s/put! :page :streams))

(secretary/defroute "/stream/:id" [id]
  (s/put! :page :stream)
  (s/put! :stream (reader/read-string id)))

(secretary/defroute "/participants" []
  (s/put! :page :participants))

;; History
;; must be called after routes have been defined

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; Data initialisation and reagent components


(defn update-local-data [d]
  (s/put! :data (reader/read-string d)))

(defn get-data []
  (GET (str (s/get :conf) ".edn") {:handler update-local-data}))

(defn on-js-reload []
  (s/update! :reload inc))

(defn mount []
  (m/render main)
  (m/render title)
  (m/render navbarNavAltMarkup))

(defn init! []
  (s/put! :conf "or2018")
  (s/put! :confname "Operations Research 2018")
  (s/put! :page :schedule)
  (hook-browser-navigation!)
  (get-data)
  (mount))

(defonce init (init!))
