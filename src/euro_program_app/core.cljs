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
  (s/put! :timeslot (reader/read-string id)) 
  (s/put! :page :timeslot))

(secretary/defroute "/session/:id" [id]
  (s/put! :session (reader/read-string id))
  (s/remove! :abstract)
  (s/put! :page :session))

(secretary/defroute "/abstract/:id" [id]
  (s/put! :abstract (reader/read-string id)) 
  (s/put! :page :session))

(secretary/defroute "/streams" []
  (s/put! :page :streams))

(secretary/defroute "/stream/:id" [id]
  (s/put! :stream (reader/read-string id)) 
  (s/put! :page :stream))

(secretary/defroute "/user/:id" [id]
  (s/put! :user (reader/read-string id)) 
  (s/put! :page :user))

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


(defn sort-map-by-fn-value [f m]
  "Returns a sorted map ordered by values of f applied to map values"
  (letfn [(compare-keys [k1 k2]
            (< (f (get m k1)) (f (get m k2))))] 
    (into (sorted-map-by compare-keys) m)))

(defn update-local-data [d]
  (let [data (reader/read-string d)
        streams (sort-map-by-fn-value :order (:streams data))
        data (assoc data :streams streams)] 
    (s/put! :data data)))

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
