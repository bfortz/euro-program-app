(ns euro-program-app.macros
  (:require [clojure.string :refer [capitalize split]]))

(defmacro render [id]
  (let [f (symbol (str "d/" id))
        strid (str id)] 
    `(r/render [~f] (.getElementById js/document ~strid))))

(defmacro nav-link [id]
  (let [link (str "#" id)
        title (->> (split (str id) #"-")
                   (map capitalize)
                   (reduce #(str %1 " " %2) ))
        pagekw (keyword id)]
    `[:a {:class (str "nav-item nav-link" 
                      (when (= (s/get :page) ~pagekw) " disabled" ))
          :href ~link} ~title]))


