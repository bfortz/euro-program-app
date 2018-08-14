(ns euro-program-app.display
  (:require [reagent.session :as s]
            [domina :as dom])
  (:require-macros [euro-program-app.macros :as m]))

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


(defn main []
  (dom/remove-class! (dom/by-id "navbarNavAltMarkup") "show") 
  [:div
   (case (s/get :page)
     :schedule (schedule)
     :timeslot (timeslot)
     :streams (streams)
     :stream (stream)
     [:h2 "Under construction."])
   [:span {:class "invisible"} (s/get :reload)]])

(defn title []
  (s/get :confname))

(defn navbarNavAltMarkup []
  [:div {:class "navbar-nav"}
   (m/nav-link schedule)
   (m/nav-link streams)
   (m/nav-link participants)])
