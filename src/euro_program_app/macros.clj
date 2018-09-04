(ns euro-program-app.macros)

(defmacro render [id]
  (let [f (symbol (str "d/" id))
        strid (str id)] 
    `(r/render [~f] (.getElementById js/document ~strid))))
