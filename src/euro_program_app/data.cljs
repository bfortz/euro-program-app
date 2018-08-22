(ns euro-program-app.data
  (:require [reagent.session :as s]
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
  "Returns a sorted map ordered by values of f applied to map values"
  (letfn [(compare-keys [k1 k2]
            (or (< (f (get m k1)) (f (get m k2)))
                (and (= (f (get m k1)) (f (get m k2)))
                     (< k1 k2))))] 
    (into (sorted-map-by compare-keys) m)))

(defn update-local-data [d]
  (let [data (reader/read-string d)
        streams (sort-map-by-fn-value :order (:streams data))
        users (sort-map-by-fn-value #(map letter-map (string/upper-case (:lastname %))) (:users data))
        data (assoc data :streams streams :users users)] 
    (when (not= (hash data) (hash (s/get :data))) 
      (s/put! :data data))))

(defn get-data []
  (let [last-fetch (s/get :last-fetch)
        now (js/Date.)]
    (when (or (nil? last-fetch) (> (- now last-fetch) 300000))
      (println (- now last-fetch))
      (s/put! :last-fetch (js/Date.))
      (GET (str (s/get :conf) ".edn") {:handler update-local-data})
      (mp/init-mysessions))))
