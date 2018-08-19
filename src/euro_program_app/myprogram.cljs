(ns euro-program-app.myprogram
  (:require [reagent.cookies :as c]
            [reagent.session :as s]
            [cljs.reader :as reader]
            [ajax.core :refer [GET]]))

(defn sort-sessions [s]
  (let [sessions (:sessions (s/get :data))] 
    (sort-by #(+ (* 100 (:timeslot (get sessions %))) (:track (get sessions %))) s)))

(defn mysessions-cookie []
  (keyword (str "mysessions-" (s/get :conf))))

(defn add-session [id]
  (GET (str "https://www.euro-online.org/" (s/get :conf) "/session/add-session-user/" id) {:handler #() :with-credentials true}) 
  (s/update! :mysessions conj (reader/read-string id))
  (s/update! :mysessions sort-sessions)
  (c/get (mysessions-cookie) (s/get :mysessions)))

(defn del-session [id]
  (GET (str "https://www.euro-online.org/" (s/get :conf) "/session/remove-session-user/" id) {:handler #() :with-credentials true}) 
  (s/update! :mysessions (partial remove #(= % (reader/read-string id))))
  (c/set! (mysessions-cookie) (s/get :mysessions)))

(defn merge-mysessions [d]
  (let [old (s/get :mysessions)
        newsessions (reader/read-string d)
        ms (-> (reduce conj old newsessions)
               (set)
               (sort-sessions))]
      (s/put! :mysessions ms)
      (c/set! (mysessions-cookie) (s/get :mysessions))))

(defn init-mysessions []
  (s/put! :mysessions (c/get (mysessions-cookie))) 
  (GET (str "https://www.euro-online.org/" (s/get :conf) "/program/mysessions" ) {:handler merge-mysessions  :with-credentials true}))
