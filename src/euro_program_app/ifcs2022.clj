(ns euro-program-app.ifcs2022
  (:require [clojure.string :as s]
            [clojure.set :refer [union]]))

(defrecord Timeslot [schedule day time sessions])

(defn ts [sch d t] (into {} (Timeslot. sch d t [])))

(def timeslots 
  {1 (ts "Tuesday, 9.30-11:00 and 11.30-13:00" "T" "A")
   2 (ts "Tuesday, 14.30-16:00 and 16.30-18:00" "T" "B")
   3 (ts "Wednesday 9.00-9.30" "W" "A") 
   4 (ts "Wednesday 9.30-10.30" "W" "B") 
   5 (ts "Wednesday 11.00-12:00" "W" "C") 
   6 (ts "Wednesday 12:00-13:00" "W" "D")
   7 (ts "Wednesday 14:30-16:00" "W" "E")
   8 (ts "Wednesday 16:30-18:00" "W" "F")
   9 (ts "Thursday 9.00-10.00" "H" "A") 
   10 (ts "Thursday 10.00-10.30" "H" "B") 
   11 (ts "Thursday 11.00-11.15" "H" "C") 
   12 (ts "Thursday 11.15-12.00" "H" "D") 
   13 (ts "Thursday 12.00-13.00" "H" "E") 
   14 (ts "Thursday 14.30-15.30" "H" "F") 
   15 (ts "Friday 9.00-10.30" "F" "A") 
   16 (ts "Friday 11.00-12.00" "F" "B") 
   17 (ts "Friday 12.00-13.00" "F" "C") 
   18 (ts "Friday 14.30-15.30" "F" "D") 
   19 (ts "Friday 15.30-16.00" "F" "E") 
   20 (ts "Friday 16.30-18.00" "F" "F") 
   21 (ts "Friday 18.00-19.30" "F" "G") 
   22 (ts "Saturday 9.00-10.30" "S" "A") 
   23 (ts "Saturday 11.00-12.00" "S" "B") 
   24 (ts "Saturday 12.00-13.00" "S" "C") 
   25 (ts "Saturday 13.00-13.30" "S" "D") 
   })

(defrecord Stream [name sessions])
(defn stream [n] (into {} (Stream. n [])))

(def streams
  {1 (stream "Tutorials")
   2 (stream "Plenaries")
   3 (stream "Semi-Plenaries")
   4 (stream "Posters")
   5 (stream "Meetings")
   "BIP"(stream "50 Years of Biplots") 
   "CL" (stream "Clustering") 
   "CoDA" (stream "Compositional Data Analysis") 
   "DS-BM" (stream "Data Science for Business and Marketing")
   "DS-B" (stream "Data Science in Biology") 
   "DS-EF" (stream "Data Science in Economics and Finance") 
   "DS-E" (stream "Data Science in Education") 
   "DS-HS" (stream "Data Science in Health Sciences") 
   "DS-SS" (stream "Data Science in Social Sciences") 
   "DR" (stream "Dimension Reduction") 
   "DR+Cl" (stream "Dimension Reduction & Clustering") 
   "FDA" (stream "Functional Data Analysis") 
   "IML" (stream "Interpretable Machine Learning")
   "ML" (stream "Machine Learning")
   "MBC" (stream "Model-based Clustering") 
   "MBC-TS" (stream "Model-based Clustering for Time Series")
   "OCC" (stream "Optimization in Classification and Clustering") 
   "RM" (stream "Robust Methods") 
   "SNA" (stream "Social Network Analysis") 
   "STDA" (stream "Spatial-Temporal Data Analysis") 
   "SPE" (stream "SPE") 
   "SLDM" (stream "Statistical Learning and Data Mining") 
   "SEM" (stream "Statistics and Econometric Methods") 
   "SC" (stream "Supervised Classification") 
   "SCT" (stream "Supervised Classification - Trees") 
   "S-ML" (stream "Supervised Machine Learning") 
   "SDA" (stream "Symbolic Data Analysis") 
   "TM" (stream "Text Mining") 
   "TS" (stream "Time Series") 
   "TSC" (stream "Time Series Classification")})

(defrecord Session [name stream chairs timeslot papers track])
(defn session [n s c ts p tr] (into {} (Session. n s c ts p tr)))

(def sessions
  (into 
    (sorted-map) 
    (zipmap 
      (map inc (range)) 
      (sort-by #(+ (* 1000 (:timeslot %)) (:track %)) 
               (map #(apply session %) 
                    [["Analysis of Data Streams" 1 [] 1 [282] 1]
                     ["Categorical Data Analysis and Visualization" 1 [] 2 [283] 1]
                     ["Opening Session" 2 [] 3 [] 0]
                     ["Keynote Dianne Cook" 2 [] 4 [256] 0]
                     ["Symbolic Data Analysis 1" "SDA" [] 5 [157, 187, 275] 5]
                     ["Functional Data Analysis 1" "FDA" [] 5 [47, 85, 235] 2]
                     ["Social Network Analysis 1" "SNA" [] 5 [13, 38, 195] 3]
                     ["50 Years of Biplots 1" "BIP" [] 5 [75, 158, 240] 4]
                     ["Statistical Learning and Data Mining 1" "SLDM" [] 5 [99, 105, 144] 8]
                     ["Clustering 1" "CL" [] 5 [50, 160, 237] 6]
                     ["Robust Methods 1" "RM" [] 5 [138, 148, 232] 7]
                     ["Model-based Clustering 1" "MBC" [] 5 [6, 18, 76] 1]
                     ["Supervised Machine Learning" "S-ML" [] 5 [40, 98] 9]
                     ["Machine Learning 1" "ML" [] 5 [129, 154, 173] 10]
                     ["Symbolic Data Analysis 2" "SDA" [] 6 [56, 59, 86] 5]
                     ["Functional Data Analysis 2" "FDA" [] 6 [84, 135, 226] 2]
                     ["Supervised Classification 1" "SC" [] 6 [126, 205, 230] 4]
                     ["Statistics and Econometric Methods" "SEM" [] 6 [61, 97, 141] 9]
                     ["Statistical Learning and Data Mining 2" "SLDM" [] 6 [27, 131, 133] 8]
                     ["Clustering 2" "CL" [] 6 [10, 25, 184 ] 6]
                     ["Robust Methods 2" "RM" [] 6 [90, 114, 132] 7]
                     ["Model-based Clustering 2" "MBC" [] 6 [24, 55, 79] 1]
                     ["Data Science in Social Sciences 1" "DS-SS" [] 6 [33, 91, 153] 3]
                     ["Machine Learning 2" "ML" [] 6 [11, 172, 189] 10]
                     ["Classification of Time Series" 3 [] 7 [81, 149, 167] 1]     
                     ["Benchmarking Challenge" 3 [] 7 [264, 265, 268, 273] 10]
                     ["Dimension Reduction" "DR" [] 8 [71, 102, 214, 219] 10]
                     ["Functional Data Analysis 3" "FDA" [] 8 [21, 47, 152, 177] 2]
                     ["Time Series" "TS" [] 8 [7, 109, 164, 258] 5] 
                     ["Supervised Classification - Trees" "SCT" [] 8 [82, 210, 239, 242] 4]
                     ["Data Science in Health Sciences" "DS-HS" [] 8 [31, 125, 254, 255] 8]
                     ["Clustering 3" "CL" [] 8 [34, 37, 41] 6]
                     ["Compositional Data Analysis 1" "CoDA" [] 8 [14, 44, 201] 9]
                     ["Model-based Clustering 3" "MBC" [] 8 [28, 257, 198, 32] 1]
                     ["Text Mining" "TM" [] 8 [30, 48, 168, 171] 3]
                     ["Symbolic Data Analysis 3" "SDA" [] 9 [107, 119, 209] 5]
                     ["Functional Data Analysis 4" "FDA" [] 9 [78, 155, 212] 2]
                     ["Optimization in Classification and Clustering 1" "OCC" [] 9 [77, 127, 231] 8]
                     ["50 Years of Biplots 2" "BIP" [] 9 [96, 145, 222] 4]
                     ["Data Science in Economics and Finance 1" "DS-EF" [] 9 [121, 139, 185] 7]
                     ["Clustering 4" "CL" [] 9 [29, 192, 221] 6]
                     ["Compositional Data Analysis 2" "CoDA" [] 9 [170, 174] 9]
                     ["Model-based Clustering 4" "MBC" [] 9 [19, 123, 204] 1]
                     ["Data Science in Social Sciences 2" "DS-SS" [] 9 [49, 113, 180] 3]
                     ["Machine Learning 3" "ML" [] 9 [2, 112, 270] 10]
                     ["Poster Session 1" 4 [] 10 [45, 54, 57, 58, 67, 73, 88, 106, 137, 140, 176, 178, 183, 218] 1]
                     ["Benchmarking Challenge Award" 2 [] 11 [] 0]
                     ["IFCS Awards" 2 [] 12 [] 0]
                     ["Keynote Edwin Diday" 2 [] 13 [281] 0]
                     ["Keynote Charles Bouveyron" 2 [] 14 [272] 0]
                     ["COVID Data Analysis" 3 [] 15 [206, 260, 262] 1]
                     ["Categorical Data Analysis and Visualization" 3 [] 15 [100, 117, 151] 10]
                     ["Presidential Address Angela Montanari" 2 [] 16 [284] 0]
                     ["Symbolic Data Analysis 4" "SDA" [] 17 [108, 147, 186] 5]
                     ["Functional Data Analysis 5" "FDA" [] 17 [43, 74, 134] 2]
                     ["Interpretable Machine Learning" "IML" [] 17 [190, 193, 250] 10]
                     ["50 Years of Biplots 3" "BIP" [] 17 [83, 103, 251] 4]
                     ["Optimization in Classification and Clustering 2" "OCC" [] 17 [15, 143, 188] 8]
                     ["Clustering 5" "CL" [] 17 [64, 95, 225] 6]
                     ["Robust Methods 3" "RM" [] 17 [5, 208, 217] 7]
                     ["Model-based Clustering 5" "MBC" [] 17 [26, 93] 1]
                     ["Spatial-Temporal Data Analysis 1" "STDA" [] 17 [122, 181, 267] 3]
                     ["SPE" "SPE" [] 17 [128, 130, 211] 9]
                     ["Keynote Genevera Allen" 2 [] 18 [271] 0]
                     ["Poster Session 2" 4 [] 19 [68, 101, 194, 213, 215, 216, 233, 234, 244, 249, 252, 269, 277, 278] 1]
                     ["Data Science for Business and Marketing" "DS-BM" [] 20 [39, 51, 159, 163] 4]
                     ["Functional Data Analysis 6" "FDA" [] 20 [65, 169, 202, 253, 280] 2]
                     ["Social Network Analysis 2" "SNA" [] 20 [36, 224, 243, 245] 3]
                     ["Data Science in Education" "DS-E" [] 20 [156, 241, 247, 248] 9]
                     ["Statistical Learning and Data Mining 3" "SLDM" [] 20 [72, 87, 89, 146] 8]
                     ["Clustering 6" "CL" [] 20 [16, 17, 23] 6]
                     ["Robust Methods 4" "RM" [] 20 [42, 94, 116, 223] 7]
                     ["Model-based Clustering 6" "MBC" [] 20 [8, 12, 110] 1]
                     ["Time Series Classification" "TSC" [] 20 [1, 22, 118, 227] 10]
                     ["IFCS Council meeting" 5 [] 21 [] 11]
                     ["Dimension Reduction" 3 [] 22 [200, 203, 220] 1]
                     ["Explainable Machine Learning" 3 [] 22 [259, 274, 279] 10]
                     ["Dimension Reduction & Clustering" "DR+Cl" [] 23 [63, 66, 166] 6]
                     ["Functional Data Analysis 7" "FDA" [] 23 [92, 124, 263] 2]
                     ["Supervised Classification 2" "SC" [] 23 [104, 115, 175] 4]
                     ["Model-based Clustering for Time Series" "MBC-TS" [] 23 [70, 136, 179] 4]
                     ["Optimization in Classification and Clustering 3" "OCC" [] 23 [182, 199, 276] 8]
                     ["Data Science in Economics and Finance 2" "DS-EF" [] 23 [35, 162, 197] 7]
                     ["Data Science in Biology" "DS-B" [] 23 [111, 161, 238] 9]
                     ["Spatial-Temporal Data Analysis 2" "STDA" [] 23 [69, 120, 142] 3]
                     ["Machine Learning 4" "ML" [] 23 [4, 191, 196] 10]
                     ["Keynote João Gama" 2 [] 24 [62] 0]
                     ["Closing Session" 2 [] 25 [] 0]
                     ])))))

(def timeslots
  (reduce (fn [acc [id s]] (update-in acc [(:timeslot s) :sessions] #(conj % id))) timeslots sessions))

(def streams
  (reduce (fn [acc [id s]] (update-in acc [(:stream s) :sessions] #(conj % id))) streams sessions))

(defrecord Abstract [id title authors abstract session keywords])

(defn process-abstract [[id session]]
  (try
    (let [s (slurp (str "resources/public/ifcs2022/" id ".txt"))
          [title authors s] (s/split s #"\r\n\r\n" 3)
          [a1 s] (when s (s/split s #"\r\nKeywords: "))
          [keywords a2] (when s (s/split s #"\r\n\r\n"))
          [authors1 authors2] (s/split authors #"[,\r\n ]+and ")
          authors (conj (s/split authors1 #",") authors2)
          authors (map s/trim (filter identity authors))
          keywords (when keywords (map (comp s/capitalize s/trim) (s/split keywords #",")))
          abstract (str a1 "\r\n" a2)]
      (into {} (Abstract. id title authors abstract session keywords)))
    (catch java.io.FileNotFoundException e (println "Abstract " id " not found"))   
    (catch Exception e (println "Abstract " id ": " (.getMessage e)))))

(defn add-session-to-abstracts [acc [id s]]
  (reduce #(assoc %1 %2 id) acc (:papers s)))

(def abstracts 
  (let [abstract-sessions (reduce add-session-to-abstracts {} sessions)] 
    (map process-abstract abstract-sessions)))

(defrecord Keyword [name sessions])

(def keyword-names 
  (sort (reduce #(union %1 (set (:keywords %2))) #{} abstracts)))

(def kw->id (zipmap keyword-names (map inc (range))))

(def abstracts
  (map #(update %1 :keywords (fn [kws] (map (fn [k] (get kw->id k)) kws))) abstracts))

(defn add-session-to-keywords [acc a]
  (reduce #(update %1 %2 (fn [s] (sort (conj s (:session a))))) acc (:keywords a)))

(def kw-sessions
  (reduce add-session-to-keywords {} abstracts))

(def keywords
  (zipmap (map inc (range)) (map #(into {} (Keyword. % (kw-sessions (get kw->id %)))) keyword-names)))

(defrecord User [firstname lastname sessions])

(def user-names 
  (sort (reduce #(union %1 (set (:authors %2))) #{} abstracts)))

(def user->id (zipmap user-names (map inc (range))))

(def abstracts
  (map #(update %1 :authors (fn [authors] (map (fn [k] (get user->id k)) authors))) abstracts))

(defn add-session-to-users [acc a]
  (reduce #(update %1 %2 (fn [s] (distinct (conj s (:session a))))) acc (:authors a)))

(def user-sessions
  (reduce add-session-to-users {} abstracts))

(defn create-user [uname]
  (let [[firstname lastname] (s/split uname #" " 2)
        sessions (user-sessions (get user->id uname))]
    (into {} (User. firstname lastname sessions))))

(def users
  (zipmap (map inc (range)) (map create-user user-names)))

(def papers (zipmap (map :id abstracts) abstracts))

(defrecord Room [room])

(def rooms 
  (zipmap (range) (map #(into {} (Room. %)) ["Grand Hall" "118" "113" "213" "260" "115" "135" "159" "211" "215" "218" "Council Room"])))

(defrecord Program [timeslots streams sessions papers keywords users rooms])

(def program (Program. timeslots streams sessions papers keywords users rooms))

(spit "resources/public/ifcs2022.edn" (into {} program))

(comment
  (filter #(nil? (:title (second %))) papers)
  (update {:k 2} :k inc)
  (process-abstract [282 1])
  (do (dorun (map process-abstract (range 1 285))) nil)
  (get user-sessions 1)
  )

