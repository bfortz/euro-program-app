(ns euro-program-app.service-worker)

(def app-cache-name "euro-program-app")

(defn- purge-old-caches [e]
  (-> js/caches
      .keys
      (.then (fn [keys]
               (->> keys
                    (map #(when-not (contains? #{app-cache-name} %)
                            (.delete js/caches %)))
                    clj->js
                    js/Promise.all)))))

(def files-to-cache 
  ["index.html"
   "conferences.edn"
   "js/compiled/euro_program_app.js" 
   "css/style.css" 
   "manifest.json"
   "images/icons-192.png"
   "images/icons-512.png"
   "https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css"
   "https://fonts.googleapis.com/icon?family=Material+Icons"
   "https://code.jquery.com/jquery-3.3.1.slim.min.js" 
   "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"
   "https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js"])

(defn- install-service-worker [e]
  (js/console.log "[ServiceWorker] Installing")
  (-> js/caches
      (.open app-cache-name)
      (.then (fn [cache]
               (js/console.log "[ServiceWorker] Caching Pages")
               (.addAll cache (clj->js files-to-cache))))
      (.then (fn []
               (js/console.log "[ServiceWorker] Successfully Installed")))))

(defn- update-cache [request response]
  (-> js/caches
      (.open app-cache-name)
      (.then (fn [cache]
               (-> (.put cache request (.clone response)))))))

(defn- fetch-and-update-cache [request]
  (-> (js/fetch request)
      (.then (fn [r]
               (update-cache request r)
               r))))

(defn fetch-event [e]
  (let [request (.-request e)
        updated-response (fetch-and-update-cache request)
        response (-> js/caches
                     (.match request)
                     (.then (fn [r]
                              (or r updated-response))))]
    (.respondWith e response)))

(.addEventListener js/self "install" #(.waitUntil % (install-service-worker %)))
(.addEventListener js/self "fetch" fetch-event)
(.addEventListener js/self "activate" #(.waitUntil % (purge-old-caches %)))
