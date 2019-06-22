(ns euro-program-app.myprogram
  (:require [reagent.cookies :as c]
            [reagent.session :as s]
            [clojure.set :as cs]
            [cljs.reader :as reader]
            [ajax.core :refer [GET]]))

(defn sort-sessions [s]
  (if-let [sessions (:sessions (s/get :data))] 
    (sort-by #(+ (* 100 (:timeslot (get sessions %))) (:track (get sessions %))) s)
    s))

(defn mysessions-cookie []
  (keyword (str "mysessions-" (s/get :conf))))

(defn retry [f id]
  (fn [_]
    (s/update! :timeout + 10000)
    (js/setTimeout #(f id) (s/get :timeout))))

(defn add-session [id]
  (when (s/get :logged) 
    (GET (str "https://www.euro-online.org/" (s/get :conf) "/session/add-session-user/" id) 
         {:handler #(s/put! :timeout 0) :error-handler (retry add-session id) :with-credentials true})) 
  (s/update! :mysessions conj (reader/read-string id))
  (s/update! :mysessions (comp sort-sessions set))
  (c/set! (mysessions-cookie) (s/get :mysessions)))

(defn del-session [id]
  (when (s/get :logged) 
    (GET (str "https://www.euro-online.org/" (s/get :conf) "/session/remove-session-user/" id) 
         {:handler #(s/put! :timeout 0) :error-handler (retry del-session id) :with-credentials true})) 
  (s/update! :mysessions (partial remove #(= % (reader/read-string id))))
  (c/set! (mysessions-cookie) (s/get :mysessions)))

(defn merge-mysessions [d]
  (let [ms (set (reader/read-string d))]
    (when (s/get :logged)
      (s/put! :mysessions ms)
      (c/set! (mysessions-cookie) (s/get :mysessions)))))

(defn logged [d]
  (s/put! :logged (reader/read-string d))
  (c/set! :logged (reader/read-string d)))

(defn init-mysessions []
  (s/put! :mysessions (sort-sessions (c/get (mysessions-cookie)))) 
  (s/put! :nologin (c/get :nologin))
  (s/put! :logged (c/get :logged))
  (GET "https://www.euro-online.org/web/accounts/logged/" {:handler logged :error-handler #() :with-credentials true})
  (GET (str "https://www.euro-online.org/" (s/get :conf) "/program/mysessions" ) {:handler merge-mysessions :error-handler #() :with-credentials true}))
