(ns euro-program-app.display
  (:require [reagent.session :as s])
  (:require-macros [euro-program-app.macros :as m]))

(defn schedule []
  (let [ts (:timeslots (s/get :data))
        activets (s/get :timeslot)] 
    [:div [:h2 "Schedule"]
     (for [t ts]
       (let [active (= (first t) activets)] 
         ^{:key (first t)}
         [:div 
          [:div {:class "row"}
           [:div {:class "col-lg-3 col-md-4 col-xs-12"} 
            [:a {:href (str "#schedule/timeslot/" (first t)) 
                 :role "button"
                 :class (str "btn btn-outline-primary col"
                             (when active " active"))
                 :aria-pressed (str active)}
             (:schedule (second t))]]
           (when active
             [:div {:class "col-12"} "TEST"])]
          ]))]))

(defn main []
  (case (s/get :page)
    :schedule (schedule)
    [:h2 "Under construction."]))

(defn title []
  (s/get :confname))

(defn navbarNavAltMarkup []
  [:div {:class "navbar-nav"}
   (m/nav-link schedule)
   (m/nav-link streams)
   (m/nav-link participants)])
