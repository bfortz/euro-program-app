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

(secretary/defroute "/conference/:conf" [conf]
  (when (not= conf (s/get :conf)) 
    (s/put! :data nil)
    (s/put! :static-pages nil)
    (s/put! :last-fetch nil)
    (s/put! :mysessions nil)
    (c/set! :conf conf)
    (s/put! :conf conf))
  (let [[_ confname] (first (filter #(= (first %) conf) (s/get :conferences)))]
    (s/put! :confname confname)) 
  (s/put! :page :schedule))

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
    (.register js/navigator.serviceWorker "service-worker.js") 
    (let [addBtn (.querySelector js/document ".add-button")
          installapp (fn [e] 
                       (let [addBtn (.querySelector js/document ".add-button")] 
                         (.preventDefault e)
                         (set! (.-display (.-style addBtn)) "block") 
                         (.addEventListener addBtn "click" 
                                            #(do
                                               (set! (.-display (.-style addBtn)) "none")
                                               (.prompt e)
                                               (.userchoice e)))))]
      (.addEventListener js/window "beforeinstallprompt" installapp)
      (set! (.-display (.-style addBtn)) "none"))))

(defn init! []
  (hook-browser-navigation!)
  (mount)
  (make-progressive!)
  (s/put! :confname "Conference Program")
  (if-let [conf (c/get :conf)]
    (do (s/put! :conf conf)
        (s/put! :page :schedule)
        (js/setTimeout (data/get-data) 100) 
        (data/get-conferences)
        (js/console.log (subs js/location.hash 1))
        (when (= (first js/location.hash) "#")
          (secretary/dispatch! (str "/" (subs js/location.hash 1)))))
    (do 
      (s/put! :data {})
      (s/put! :page :select-conference))))

(defonce init (init!))

(comment
  (let [conf "or2018"] 
    (s/put! :last-fetch nil)
    (s/put! :conf conf)
    (c/set! :conf conf))
  (c/remove! :conf)
  (c/get :conf)
  (s/get :conferences)
  (s/get :last-fetch)
  (s/get :page)
  (s/put! :confname "TEST")
  (:timeslots (s/get :data)))
