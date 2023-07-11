(ns euro-program-app.display
  (:require [euro-program-app.data :as d]
            [euro-program-app.myprogram :as mp]
            [reagent.session :as s]
            [reagent.cookies :as c]
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

(defn abstract-authors [p]
  (reduce #(conj (conj %1 ", ") (user %2)) 
          (let [fa (first (:authors p))] 
            [vector :span (user fa)])
          (rest (:authors p))))

(defn keywords-paper [p]
  (let [kwnames (:keywords (s/get :data))
        kwlist (concat (:keywords p) (->> (list :keyword1 :keyword2 :keyword3)   
                                          (map #(% p))))
        kws (->> kwlist
                 (filter identity)
                 (filter #(not= 0 %))  
                 (map #(vector :a {:href (str "#keyword/" %)} (get-in kwnames [% :name]))))]
    (reduce #(conj %1 ", " %2) (vector :span (first kws)) (rest kws))))

(defn paper-details [paperid]
  (let [d (s/get :data)
        p (get-in d [:papers paperid])
        id (:session p)
        s (get-in d [:sessions id]) 
        t (get-in d [:timeslots (:timeslot s)])]
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
        (if-let [a (:acronym s)] a (str (:day t) (:time t) "-" (:track s))) ": " [:b (:name s)]]] 
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
       [:p (abstract-authors p)]]]
     [:div {:class "row"} 
      [:div {:class "col"} 
       [:p [:b "Keywords: "] (keywords-paper p)]]]
     [:div {:class "row"}
      [:div {:class "col abstract"} 
       (:abstract p)]]]))

(defn session-detail []
  (let [d (s/get :data)
        paperid (s/get :abstract)
        id (if paperid
             (:session (get (:papers d) paperid)) 
             (s/get :session))
        s (get-in d [:sessions id]) 
        rooms (:rooms d)
        t (get-in d [:timeslots (:timeslot s)]) 
        stream (get-in d [:streams (:stream s)])
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
        (if-let [a (:acronym s)] a (str (:day t) (:time t) "-" (:track s))) ": " [:b (:name s)]] 
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
       (paper-details paperid)
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
                 (abstract-authors p)])))]]])]))

(defn session [id]
  (let [d (s/get :data)
        s (get-in d [:sessions id]) 
        rooms (:rooms d)
        t (get-in d [:timeslots (:timeslot s)]) 
        stream (get-in d [:streams (:stream s)])
        r (if (nil? (:specialroom s))
            (:room (get rooms (:track s)))
            (:specialroom s))]
    ^{:key (str "S" id)}
    [:div {:role "button"
           :class "btn-program col"}
     [:div {:class "row session"}
      [:div {:class "col-3 col-md-3 col-lg-2"} 
       (if-let [a (:acronym s)] a (str (:day t) (:time t) "-" (:track s))) [:br] r]
      [:div {:class "col"} 
       [:a {:href (str "#session/" id)} 
        [:b {:style {:color "red"}} (:name s)]] [:br] 
       [:a {:href (str "#stream/" (:stream s))} 
        [:i {:style {:color "black"}} (:name stream)]]]
      [:div {:class "col-2"} 
       [:a {:href (str (if (empty? (filter #(= id %) (s/get :mysessions)))
                         "#addsession/"
                         "#delsession/")
                       id)
            :role "button"
            :class "btn btn"}
        (if (empty? (filter #(= id %) (s/get :mysessions)))
          [:i {:class "material-icons"} "star_border"]   
          [:i {:class "material-icons"} "star"])]]]
     (case (s/get :page)
       :user (let [uid (s/get :user)]
               (list
                 (when (some #(= uid %) (:chairs s))
                   ^{:key (str "CH" id)}
                   [:div {:class "row session pt-1"} 
                    [:div {:class "col-4 col-md-3 col-lg-2"}]
                    [:div {:class "col"} 
                     [:b "Session chair"]]] ) 
                 (let [papers (filter 
                                (fn [p] (some #(= uid %) (:authors (get (:papers d) p)))) 
                                (:papers s))]
                   ^{:key (str "PAPERS" id)}
                   [:ul {:class "container pt-2"}
                    (doall
                      (for [pid papers]
                        (let [p (get (:papers d) pid)] 
                          ^{:key (str "P" pid)}
                          [:li {:style {:color "green"}}
                           [:a {:href (str "#abstract/" pid) :style {:color "green"}} 
                            (if (= (first (:authors p)) uid)
                              [:b (:title p)]
                              (:title p))]])))])))
       :keyword (let [kid (s/get :keyword)]
                  (let [papers (filter 
                                 (fn [pid] 
                                   (let [p (get (:papers d) pid)] 
                                     (some #(= kid %) (concat (:keywords p)
                                                              (list (:keyword1 p) 
                                                                    (:keyword2 p)
                                                                    (:keyword3 p)))))) 
                                 (:papers s))]
                    [:ul {:class "container pt-2"}
                     (doall
                       (for [pid papers]
                         (let [p (get (:papers d) pid)] 
                           ^{:key (str "P" pid)}
                           [:li {:style {:color "green"}}
                            [:a {:href (str "#abstract/" pid) :style {:color "green"}} 
                             (:title p)]])))]))
       "")])) 

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
     [:div {:id "sessions"}
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
                :class "btn btn-program col"}
            (:schedule t)]]]))]))

(defn user-detail []
  (let [d (s/get :data)
        id (s/get :user)
        u (get (:users d) id)] 
    [:div
     [:div {:class "row"} 
      [:div {:class "col text-center"} [:h3 [:a {:href "#authors"} "Authors"]]]]
     [:div {:class "row"} 
      [:div {:class "col text-center"} [:h2 (:firstname u) " " (:lastname u)]]]
     [:div {:class "row"} 
      [:div {:class "col text-center"} (:department u) [:br] (:institution u)]] 
     [:div {:id "sessions" :class "mt-3"}
      (doall (map session (:sessions u)))]]))

(defn keyword-detail []
  (let [d (s/get :data)
        id (s/get :keyword)
        k (get (:keywords d) id)] 
    [:div
     [:div {:class "row"} 
      [:div {:class "col text-center"} [:h3 [:a {:href "#keywords"} "Keywords"]]]]
     [:div {:class "row"} 
      [:div {:class "col text-center"} [:h2 (:name k)]]]
     [:div {:id "sessions"}
      (doall (map session (:sessions k)))]]))

(defn stream []
  (let [d (s/get :data)
        id (s/get :stream)
        s (get (:streams d) id)] 
    [:div
     [:div {:class "row"} 
      [:div {:class "col text-center"} [:h3 [:a {:href "#streams"} "Streams"]]]]
     [:div {:class "row"} 
      [:div {:class "col text-center"} [:h2 (:name s)]]]
     [:div {:id "sessions"}
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
     [:div {:id "sessions"}
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
                :class "btn btn-program col session"}
            (:name s)]]]))]))

(defn authors []
  (let [users (:users (s/get :data))
        fl (s/get :first)]
    (if fl
      (let [u (filter #(= fl (d/up-first %)) users)]
        [:div
         [:h2 [:a {:href "#authors"} "Authors"]]
         [:ul
          (doall
            (for [id (keys u)] 
              ^{:key (str "P" id)}
              [:li
               (user-last id)]))]])
      (let [first-letter (apply sorted-set (map d/up-first users))]
        [:div 
         [:h2 "Authors"]
         [:div {:class "row"}
          (doall
            (for [l first-letter]
              ^{:key (str "P" l)}
              [:div {:class "col-2 col-md-1 text-center"}
               [:a {:href (str "#authors/" l) 
                    :role "button"
                    :class "btn btn-program letter"} l]]))]]))))

(defn keywords []
  (let [kws (:keywords (s/get :data))]
    [:div
     [:h2 "Keywords"]
     [:div {:class "row"}
      [:div {:class "col"}
        [:ul
         (for [[id k] kws]
           ^{:key (str "K" id)}
           [:li [:a {:href (str "#keyword/" id) } (:name k)]])]]]]))

(defn login []
  (when (and (s/get :conf) 
             (s/get :data)
             (not (s/get :nologin)) 
             (not (s/get :logged)))
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
            :class "btn btn-primary"} "Never ask"]]]]))

(defn static []
  (d/get-static-page)
  "")

(defn select-conference []
  (let [confs (s/get :conferences)]
    [:div {:class "row"}
     [:div {:class "col"}
      [:p "Please choose a conference below."]
      [:ul
       (for [[conf confname] confs]
         ^{:key (str "C" conf)}
         [:li [:a {:href (str "#conference/" conf)} confname]])]]]))

(defn main []
  (dom/remove-class! (dom/by-id "navbarNavAltMarkup") "show") 
  (set! (.-scrollTop (.-body js/document)) 0)
  (set! (.-scrollTop (.-documentElement js/document)) 0) 
  (when-let [elt (dom/by-id "sessions")] (set! (.-scrollTop elt) 0))
  (if (s/get :conf)
    (js/setTimeout d/get-data 1000)
    (d/get-conferences))
  (if (s/get :data) 
    [:div
     [:button {:class "add-button btn btn-primary"} "Add to home screen"]
     (login)
     (case (s/get :page)
       :schedule (schedule)
       :timeslot (timeslot)
       :streams (streams)
       :stream (stream)
       :session (session-detail)
       :user (user-detail)
       :authors (authors)
       :keywords (keywords)
       :keyword (keyword-detail)
       :my-program (my-program)
       :static (static)
       :select-conference (select-conference)
       [:h2 "Under construction."])
     (when (= (s/get :page) :static)
       [:div {:dangerouslySetInnerHTML {:__html (s/get :static-page)}}])
     [:span {:class "invisible"} (s/get :reload)]]
    [:div 
     [:button {:class "add-button btn btn-primary"} "Add to home screen"]
     [:h2 "Loading program data. Please wait..."]]))

(defn title []
  [:span (when-let [logo (s/get :logo)]
           [:img {:src logo :height "40px" :class "bg-white mr-2"}]) 
   (s/get :confname)])

(defn nav-link [id]
  (let [link (str "#" id)
        title (->> (string/split id #"-")
                   (map string/capitalize)
                   (reduce #(str %1 " " %2)))
        pagekw (keyword id)]
    ^{:key (str "M" id)} 
    [:li {:class "nav-item"} 
     [:a {:class (str "nav-link"
                      (when (= (s/get :page) pagekw) " disabled"))
          :href link} title]]))

(defn nav-dd-link [id]
  (let [link (str "#" id)
        title (->> (string/split id #"-")
                   (map string/capitalize)
                   (reduce #(str %1 " " %2)))
        pagekw (keyword id)]
    ^{:key (str "MD" id)} 
    [:a {:class "dropdown-item" :href link} title]))

(defn navbarNavAltMarkup []
  [:ul {:class "navbar-nav"}
   (nav-link "schedule")
   (nav-link "my-program")
   (nav-link "streams")
   (nav-link "authors")
   (nav-link "keywords")
   [:div {:class "d-inline-flex d-md-none d-xl-inline-flex navbar-nav"}
      (doall (map (comp nav-link name first) (s/get :static-pages))) 
      (nav-link "select-conference")]
   [:div {:class "d-none d-md-block d-xl-none"} 
    [:li {:class "nav-item dropdown"}
     [:a {:class "nav-link dropdown-toggle" :href "#" :id "navbarDropdownMenu"
          :role "button" :data-toggle "dropdown" :aria-haspopup "true" 
          :aria-expanded "false"} "More"]
     [:div {:class "dropdown-menu" 
            :aria-labelledby "navbarDropdownMenu"}
      (doall (map (comp nav-dd-link name first) (s/get :static-pages)))     
      (nav-dd-link "select-conference")]]]])

