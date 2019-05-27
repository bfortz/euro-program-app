(ns euro-program-app.data
  (:require [reagent.session :as s]
            [reagent.core :as r]
            [reagent.cookies :as c]
            [euro-program-app.edn :as reader]
            [euro-program-app.myprogram :as mp]
            [secretary.core :as secretary :include-macros true]
            [clojure.string :as string]
            [ajax.core :refer [GET]]))

(defn letter-map [l] 
  (let [lm {"Ä" "A", "Á" "A", "ä" "a","Č" "C", "Ç" "C", "ç" "c", "É" "E", "é" "e", "È" "E", "è" "e", "Ö" "O", "ö" "o", "Ş" "S", "ş" "s", "Ü" "U", "ü" "u", "Ż" "Z", "Ž" "Z" }]
    (get lm l l)))

(defn up-first [u]
  (->> (second u)
       (:lastname)
       (first)
       (string/upper-case)
       (letter-map)))

(defn sort-map-by-fn-value [f m]
  "Returns a sorted map ordered by values of f applied
  to map values"
  (letfn [(ck [k1 k2]
            (or (< (f (get m k1)) (f (get m k2)))
                (and (= (f (get m k1)) (f (get m k2)))
                     (< k1 k2))))] 
    (into (sorted-map-by ck) m)))

(defn add-ucl [m u] 
  (let [[id x] u] 
    (assoc-in m [id :ucl] (apply str (map letter-map (string/upper-case (str (:lastname x) " " (:firstname x))))))))

(defn async-users-update []
  (let [data (s/get :data)
        users (reduce add-ucl (:users data) (:users data))
        users (sort-map-by-fn-value :ucl users)]
    (s/assoc-in! [:data :users] users)))

(defn async-data-update []
  (let [data (s/get :data)
        streams (sort-map-by-fn-value :name (:streams data))
        keywords (sort-map-by-fn-value :name (:keywords data))]
    (s/assoc-in! [:data :streams] streams)
    (s/assoc-in! [:data :keywords] keywords)
    (js/setTimeout async-users-update 500)))
  
(defn update-local-data [d]
  (let [h (hash d)
        conf (s/get :conf)
        oldh (s/get :data-hash)]
    (let [[_ confname] (first (filter #(= (first %) conf) (s/get :conferences)))]
      (s/put! :confname confname)) 
    (when (not= h oldh)
      (s/put! :data-hash h)
      (s/put! :data (reader/read-string d))
      (js/setTimeout async-data-update 500))
    (mp/init-mysessions)))

(defn check-app-version [d]
  (let [h (hash d)
        oldh (c/get :app-version)]
    (when (not= h oldh)
      (c/set! :app-version h)
      (.reload (.-location js/window)))))

(defn static-pages [d]
  (let [pages (reader/read-string d)]
    (s/put! :static-pages pages)
    (doseq [[p f] pages] 
      (secretary/defroute (str "/" (name p)) []
        (s/put! :url f)
        (s/put! :page :static))
      (when (= js/location.hash (str "#" (name p)))
        (secretary/dispatch! (str "/" (name p)))))))

(defn conferences [d]
  (let [confs (reader/read-string d)]
    (s/put! :conferences confs)))

(defn get-conferences []
  (GET "conferences.edn" {:handler conferences}))

(defn get-data []
  (let [timeout 300000
        last-fetch (s/get :last-fetch)
        now (js/Date.)]
    (when (or (nil? last-fetch) (> (- now last-fetch) timeout))
      (s/put! :last-fetch (js/Date.))
      (get-conferences)
      (GET (str (s/get :conf) ".edn") {:handler update-local-data})
      (GET "js/compiled/euro_program_app.js" {:handler check-app-version})
      (GET (str (s/get :conf) "/pages.edn") {:handler static-pages 
                                             :error-handler #(js/console.log "Ignoring non-existent pages files")}) 
      (js/setTimeout get-data timeout))))

(defn display-static-page [d]
  (s/put! :static-page d))

(defn get-static-page []
  (GET (str (s/get :conf) "/" (s/get :url)) {:handler display-static-page}))
