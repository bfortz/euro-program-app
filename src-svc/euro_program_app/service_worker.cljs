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
   "or2018.edn"
   "js/compiled/euro_program_app.js" 
   "css/style.css" 
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

(defn- fetch-cached [request]
  (-> js/caches
      (.match request)
      (.then (fn [response]
               (or response (js/fetch request))))))

(defn- update-cache [request h]
  (-> js/caches
      (.open app-cache-name)
      (.then (fn [cache]
               (-> (js/fetch request)
                   (.then (fn [response]
                            (-> (.text (.clone response))
                                (.then (fn [cr] 
                                         (when (not= h (hash cr))
                                           (js/console.log "Updating cache for " (.-url request))
                                           (.put cache request response))))))))))))

(defn fetch-and-update-event [e]
  (let [request (.-request e)]
    ;; (js/console.log "[ServiceWorker] Fetch" (-> e .-request .-url))
    (.respondWith e (-> (fetch-cached request)
                        (.then (fn [r]
                                 (-> (.text (.clone r))
                                     (.then #(update-cache request (hash %))))
                                 r))))))

(.addEventListener js/self "install" #(.waitUntil % (install-service-worker %)))
(.addEventListener js/self "fetch" fetch-and-update-event)
(.addEventListener js/self "activate" #(.waitUntil % (purge-old-caches %)))
