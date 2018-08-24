(ns euro-program-app.data
  (:require [reagent.session :as s]
            [reagent.cookies :as c]
            [cljs.reader :as reader]
            [euro-program-app.myprogram :as mp]
            [clojure.string :as string]
            [ajax.core :refer [GET]]))

(defn letter-map [l] 
  (let [lm {"Ä" "A", "ä" "a", "Ç" "C", "ç" "c", "Ö" "O", "ö" "o", "Ş" "S", "ş" "s", "Ü" "U", "ü" "u" }]
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

(def ucl (memoize (fn [u] (map letter-map (string/upper-case (:lastname u))))))

(defn update-local-data [d]
  (let [h (hash d)
        oldh (s/get :data-hash)]
    (when (not= h oldh)
      (s/put! :data-hash h)
      (let [data (reader/read-string d)
            streams (sort-map-by-fn-value :order (:streams data))
            users (sort-map-by-fn-value ucl (:users data))
            keywords (sort-map-by-fn-value :name (:keywords data))
            data (assoc data :streams streams :users users :keywords keywords)] 
        (s/put! :data data)
        (when oldh 
          (.reload (.-location js/window)))))))

(defn check-app-version [d]
  (let [h (hash d)
        oldh (c/get :app-version)]
    (when (not= h oldh)
      (c/set! :app-version h)
      (.reload (.-location js/window)))))

(defn get-data []
  (let [timeout 300000
        last-fetch (s/get :last-fetch)
        now (js/Date.)]
    (when (or (nil? last-fetch) (> (- now last-fetch) timeout))
      (s/put! :last-fetch (js/Date.))
      (GET (str (s/get :conf) ".edn") {:handler update-local-data})
      (GET "js/compiled/euro_program_app.js" {:handler check-app-version})
      (mp/init-mysessions)
      (js/setTimeout get-data timeout))))
