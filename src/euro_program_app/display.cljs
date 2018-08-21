(ns euro-program-app.display
  (:require [reagent.session :as s]
            [clojure.string :as string]
            [domina :as dom])
  (:require-macros [euro-program-app.macros :as m]))

(defn user [id]
  (let [d (s/get :data)
        u (get (:users d) id)]
    [:a {:href (str "#user/" id)} (str (:firstname u) " " (:lastname u))]))

(defn user-last [id]
  (let [d (s/get :data)
        u (get (:users d) id)]
    [:a {:href (str "#user/" id)} (str (:lastname u) ", " (:firstname u))]))

(defn authors [p]
  (reduce #(conj (conj %1 ", ") (user %2)) 
          (let [fa (first (:authors p))] 
            [vector :span (user fa)])
          (rest (:authors p))))

(defn session-detail []
  (let [d (s/get :data)
        paperid (s/get :abstract)
        paper (get (:papers d) paperid)
        id (if paperid
             (:session paper) 
             (s/get :session))
        s (get (:sessions d) id) 
        rooms (:rooms d)
        t (get (:timeslots d) (:timeslot s))
        stream (get (:streams d) (:stream s))
        r (if (nil? (:specialroom s))
            (:room (get rooms (:track s)))
            (:specialroom s))
        chairs (:chairs s)]
    [:div
     [:div {:class (str "row" 
                        (when paperid " d-none d-md-flex"))}
      [:div {:class "col-2 col-md-1 pl-0 ml-0 text-left"}
       [:a {:href (str (if (empty? (filter #(= id %) (s/get :mysessions)))
                         "#addsession/"
                         "#delsession/")
                       id)
            :role "button"
            :class "btn btn"}
        (if (empty? (filter #(= id %) (s/get :mysessions)))
         [:i {:class "material-icons"} "star_border"]   
         [:i {:class "material-icons"} "star"])]]
      [:div {:class "col-10 mb-3"}
       [:a {:href (str "#timeslot/" (:timeslot s))} (:schedule t)] [:br]
       "Room: " [:i r][:br] 
       "Stream: " [:a {:href (str "#stream/" (:stream s)) :class "text-black"} 
                   (:name stream)]]
      [:div {:class "col"}
       [:h2 {:class (if paperid "d-none" "") :style {:color "red"}}
        (:day t) (:time t) "-" (:track s) ": " [:b (:name s)]] 
       [:div {:class (if paperid "d-none" "")}
        (if (= (count chairs) 1)
          [:p "Chair: " (user (first chairs))]
          [:span "Chairs:"
           [:ul
            (doall 
              (for [c (:chairs s)]
                ^{:key (str "C" c)}
                [:li (user c)]))]])]]]
     [:div {:class "row"}]
     (if paperid
       (let [p (get (:papers d) paperid)]
         [:div
          [:div {:class "row mb-3"}
           [:div {:class "col-2"} 
            (if (= paperid (first (:papers s))) 
              ""
              (let [previd (loop [papers (:papers s)]
                             (if (= paperid (fnext papers))
                               (first papers)
                               (recur (rest papers))))] 
                [:a {:href (str "#abstract/" previd)
                     :role "button"
                     :class "btn btn-primary btn-sm"}
                 [:i {:class "material-icons"} "arrow_back"]]))]
           [:div {:class "col text-center"}
            [:a {:href (str "#session/" id) :style {:color "red"}} 
             (:day t) (:time t) "-" (:track s) ": " [:b (:name s)]]] 
           [:div {:class "col-2 text-right"} 
            (if (= paperid (last (:papers s))) 
              ""
              (let [nextid (loop [papers (:papers s)]
                             (if (= paperid (first papers))
                               (fnext papers)
                               (recur (rest papers))))] 
                [:a {:href (str "#abstract/" nextid)
                     :role "button"
                     :class "btn btn-primary btn-sm"}
                 [:i {:class "material-icons"} "arrow_forward"]]))]]
          [:div {:class "row"}
           [:div {:class "col"} 
            [:h3 (:title p)]]]
          [:div {:class "row"} 
           [:div {:class "col"} 
            [:p (authors p)]]]
          [:div {:class "row"}
           [:div {:class "col abstract"} 
            (:abstract p)]]])
       [:div {:class "row"}
        [:div {:class "col"} 
         [:ol
          (doall 
            (for [pid (:papers s)]
              (let [p (get (:papers d) pid)]
                ^{:key (str "P" pid)}
                [:li
                 [:a {:href (str "#abstract/" pid) :style {:color "black"} } 
                  [:i (:title p)]] [:br]
                 (authors p)])))]]])]))

(defn session [id]
  (let [d (s/get :data)
        s (get (:sessions d) id) 
        rooms (:rooms d)
        t (get (:timeslots d) (:timeslot s))
        stream (get (:streams d) (:stream s))
        r (if (nil? (:specialroom s))
            (:room (get rooms (:track s)))
            (:specialroom s))]
    ^{:key (str "S" id)}
    [:a {:href (str "#session/" id) 
         :role "button"
         :class "btn btn-outline-primary col"}
     [:div {:class "row session"}
      [:div {:class "col-4 col-md-3 col-lg-2"} 
       (str (:day t) (:time t) "-" (:track s)) [:br] r]
      [:div {:class "col"} [:b {:style {:color "red"}} (:name s)] [:br] 
       [:i {:style {:color "black"}} (:name stream)]]]]))

(defn timeslot []
  (let [d (s/get :data)
        id (s/get :timeslot)
        t (get (:timeslots d) id)] 
    [:div
     [:div {:class "row"} 
      [:div {:class "col text-center"} [:h3 [:a {:href "#schedule"} "Schedule"]]]]
     [:div {:class "row"} 
      [:div {:class "col-2"} 
       (when (get (:timeslots d) (dec id))
         [:a {:href (str "#timeslot/" (dec id))
              :role "button"
              :class "btn btn-primary btn-sm"}
          [:i {:class "material-icons"} "arrow_back"]])]
      [:div {:class "col-8 text-center"} [:h3 (:schedule t)]] 
      [:div {:class "col-2 text-right"} 
       (when (get (:timeslots d) (inc id))
         [:a {:href (str "#timeslot/" (inc id))
              :role "button"
              :class "btn btn-primary btn-sm"}
          [:i {:class "material-icons"} "arrow_forward"]])]]
     [:div {:class "sessions"}
      (doall (map session (:sessions t)))]]))

(defn schedule []
  (let [d (s/get :data)]
    [:div [:h2 "Schedule"]
     (doall
       (for [[id t] (:timeslots d)]
         ^{:key (str "T" id)}
         [:div {:class "row"}
          [:div {:class "col-lg-3 col-md-6"} 
           [:a {:href (str "#timeslot/" id) 
                :role "button"
                :class "btn btn-outline-primary col"}
            (:schedule t)]]]))]))

(defn user-detail []
  (let [d (s/get :data)
        id (s/get :user)
        u (get (:users d) id)] 
    [:div
     [:div {:class "row"} 
      [:div {:class "col text-center"} [:h3 [:a {:href "#participants"} "Participants"]]]]
     [:div {:class "row"} 
      [:div {:class "col text-center"} [:h2 (:firstname u) " " (:lastname u)]]]
     [:div {:class "sessions"}
      (doall (map session (:sessions u)))]]))

(defn stream []
  (let [d (s/get :data)
        id (s/get :stream)
        s (get (:streams d) id)] 
    [:div
     [:div {:class "row"} 
      [:div {:class "col text-center"} [:h3 [:a {:href "#streams"} "Streams"]]]]
     [:div {:class "row"} 
      [:div {:class "col text-center"} [:h2 (:name s)]]]
     [:div {:class "sessions"}
      (doall (map session (:sessions s)))]]))

(defn my-program []
  (let [s (s/get :mysessions)]
    [:div
     [:div {:class "row"} 
      [:div {:class "col"} 
       [:h2 "My Program"]]]
     [:div {:class "row"}
      [:div {:class "col"}
       [:p "To add or remove a session from your program, click on the star on the session page."]]]
     [:div {:class "sessions"}
      (doall (map session s))]]))

(defn streams []
  (let [d (s/get :data)]
    [:div [:h2 "Streams"]
     (doall
       (for [[id s] (:streams d)]
         ^{:key (str "ST" id)}
         [:div {:class "row"}
          [:div {:class "col"} 
           [:a {:href (str "#stream/" id) 
                :role "button"
                :class "btn btn-outline-primary col session"}
            (:name s)]]]))]))

(defn letter-map [l] 
  (let [lm {"Ä" "A", "ä" "a", "Ç" "C", "ç" "c", "Ö" "O", "ö" "o", "Ş" "S", "ş" "s", "Ü" "U", "ü" "u" }]
    (get lm l l)))

(defn up-first [u]
  (->> (second u)
       (:lastname)
       (first)
       (string/upper-case)
       (letter-map)))

(defn participants []
  (let [users (:users (s/get :data))
        fl (s/get :first)]
    (if fl
      (let [u (filter #(= fl (up-first %)) users)]
        [:div
         [:h2 [:a {:href "#participants"} "Participants"]]
         [:ul
          (doall
            (for [id (keys u)] 
              ^{:key (str "P" id)}
              [:li
               (user-last id)]))]])
      (let [first-letter (apply sorted-set (map up-first users))]
        [:div 
         [:h2 "Participants"]
         [:div {:class "row"}
          (doall
            (for [l first-letter]
              ^{:key (str "P" l)}
              [:div {:class "col-2 col-md-1 text-center"}
               [:a {:href (str "#participants/" l) 
                    :role "button"
                    :class "btn btn-outline-primary letter"} l]]))]]))))

(defn login []
  (when-not (s/get :nologin) 
    (when-not (s/get :logged)
      [:div {:class "login col-md-6"}
       [:div {:class "row"}
        [:div {:class "col"}
         [:p "To synchronize your personalized program across your devices, please log in."]]]
       [:div {:class "row"}
        [:div {:class "col"}
         [:a {:href (str "https://www.euro-online.org/web/accounts/login/?next=" 
                         js/location.href)
              :role "button"
              :class "btn btn-primary"} "Log in"]]
        [:div {:class "col"}
         [:a {:href "#notnow" 
              :role "button"
              :class "btn btn-primary"} "Not now"]]   
        [:div {:class "col"}
         [:a {:href "#never" 
              :role "button"
              :class "btn btn-primary"} "Never ask"]]]])))

(defn main []
  (dom/remove-class! (dom/by-id "navbarNavAltMarkup") "show") 
  (when (s/get :data) 
    [:div
     (login)
     (case (s/get :page)
       :schedule (schedule)
       :timeslot (timeslot)
       :streams (streams)
       :stream (stream)
       :session (session-detail)
       :user (user-detail)
       :participants (participants)
       :my-program (my-program)
       [:h2 "Under construction."])
     [:span {:class "invisible"} (s/get :reload)]]))

(defn title []
  (s/get :confname))

(defn navbarNavAltMarkup []
  [:div {:class "navbar-nav"}
   (m/nav-link schedule)
   (m/nav-link my-program)
   (m/nav-link streams)
   (m/nav-link participants)])
