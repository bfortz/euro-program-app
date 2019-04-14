(ns euro-program-app.core
  (:require [reagent.session :as s]
            [reagent.core :as r]
            [reagent.cookies :as c]
            [cljs.reader :as reader]
            [euro-program-app.display :as d]
            [euro-program-app.myprogram :as mp]
            [euro-program-app.data :as data]
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
  (if (s/get :conf) 
    (s/put! :page :schedule)
    (s/put! :page :select-conference)))

(secretary/defroute "/schedule" []
  (s/put! :page :schedule))

(secretary/defroute "/select-conference" []
  (s/put! :page :select-conference))

(secretary/defroute "/timeslot/:id" [id]
  (s/put! :timeslot (reader/read-string id)) 
  (s/put! :page :timeslot))

(secretary/defroute "/session/:id" [id]
  (s/put! :session (reader/read-string id))
  (s/remove! :abstract)
  (s/put! :page :session))

(secretary/defroute "/addsession/:id" [id]
  (mp/add-session id))

(secretary/defroute "/delsession/:id" [id]
  (mp/del-session id))

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
  (s/remove! :first)
  (s/put! :page :participants))

(secretary/defroute "/participants/:letter" [letter]
  (s/put! :first letter)
  (s/put! :page :participants))

(secretary/defroute "/keywords" []
  (s/put! :page :keywords))

(secretary/defroute "/keyword/:id" [id]
  (s/put! :keyword (reader/read-string id))
  (s/put! :page :keyword))

(secretary/defroute "/my-program" []
  (s/put! :page :my-program))

(secretary/defroute "/notnow" []
  (s/put! :nologin true))

(secretary/defroute "/never" []
  (s/put! :nologin true)
  (c/set! :nologin true))


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

(defn on-js-reload []
  (s/update! :reload inc))

(defn mount []
  (m/render main)
  (m/render title)
  (m/render navbarNavAltMarkup))

(defn- make-progressive! []
  (when js/navigator.serviceWorker
    (.register js/navigator.serviceWorker "service-worker.js")))

(defn init! []
  (when-let [conf (c/get :conf)]
    (s/put! :conf conf))
  (if (s/get :conf)
    (do 
      (s/put! :page :schedule)
      (data/get-data))
    (do 
      (s/put! :data {})
      (s/put! :page :select-conference)))
  (hook-browser-navigation!)
  (mount)
  (make-progressive!))

(defonce init (init!))

(comment
  (let [conf "ifors"] 
    (s/put! :last-fetch nil)
    (s/put! :conf conf)
    (c/set! :conf conf))
  (c/remove! :conf)
  (c/get :conf)
  (s/get :conf)
  (s/get :last-fetch)
  (s/get :page)
  (s/put! :confname "TEST")
  )
