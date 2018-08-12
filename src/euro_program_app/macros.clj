(ns euro-program-app.macros
  (:require [clojure.string :refer [capitalize]]))

(defmacro render [id]
  (let [f (symbol (str "d/" id))
        strid (str id)] 
    `(r/render [~f] (.getElementById js/document ~strid))))

(defmacro nav-link [id]
  (let [link (str "#" id)
        title (capitalize (str id))
        pagekw (keyword id)]
    `[:a {:class (str "nav-item nav-link" 
                      (when (= (s/get :page) ~pagekw) " disabled" ))
          :href ~link} ~title]))
