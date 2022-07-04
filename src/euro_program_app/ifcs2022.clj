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
   11 (ts "Thursday 11.00-13.00" "H" "C") 
   12 (ts "Thursday 14.30-15.30" "H" "D") 
   13 (ts "Friday 9.00-10.30" "F" "A") 
   14 (ts "Friday 11.00-12.00" "F" "B") 
   15 (ts "Friday 12.00-13.00" "F" "C") 
   16 (ts "Friday 14.30-15.30" "F" "D") 
   17 (ts "Friday 15.30-16.00" "F" "E") 
   18 (ts "Friday 16.30-18.00" "F" "F") 
   19 (ts "Friday 18.00-19.30" "F" "G") 
   20 (ts "Saturday 9.00-10.30" "S" "A") 
   21 (ts "Saturday 11.00-12.00" "S" "B") 
   22 (ts "Saturday 12.00-13.00" "S" "C") 
   23 (ts "Saturday 13.00-13.30" "S" "D") 
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
    {1 (session "Analysis of Data Streams" 1 [] 1 [282] 1)
     2 (session "Categorical Data Analysis and Visualization" 1 [] 2 [283] 1)
     3 (session "Opening Session" 2 [] 3 [] 1)
     4 (session "Keynote Dianne Cook" 2 [] 4 [256] 1)
     5 (session "Symbolic Data Analysis 1" "SDA" [] 5 [157, 187, 275] 1)
     6 (session "Functional Data Analysis 1" "FDA" [] 5 [47, 85, 235] 2)
     7 (session "Social Network Analysis 1" "SNA" [] 5 [13, 38, 195] 3)
     8 (session "50 Years of Biplots 1" "BIP" [] 5 [75, 158, 240] 4)
     9 (session "Statistical Learning and Data Mining 1" "SLDM" [] 5 [99, 105, 144] 5)
     10 (session "Clustering 1" "CL" [] 5 [50, 160, 237] 6)
     11 (session "Robust Methods 1" "RM" [] 5 [138, 148, 232] 7)
     12 (session "Model-based Clustering 1" "MBC" [] 5 [6, 18, 76] 8)
     13 (session "Supervised Machine Learning 1" "S-ML" [] 5 [4, 98, 216] 9)
     14 (session "Machine Learning 1" "ML" [] 5 [129, 154, 173] 10)
     15 (session "Symbolic Data Analysis 2" "SDA" [] 6 [56, 59, 86] 1)
     16 (session "Functional Data Analysis 2" "FDA" [] 6 [84, 135, 226] 2)
     17 (session "Supervised Classification 1" "SC" [] 6 [126, 205, 230] 3)
     18 (session "Statistics and Econometric Methods" "SEM" [] 6 [61, 97, 141] 4)
     19 (session "Statistical Learning and Data Mining 2" "SLDM" [] 6 [27, 131, 133] 5)
     20 (session "Clustering 2" "CL" [] 6 [10, 25, 184 ] 6)
     21 (session "Robust Methods 2" "RM" [] 6 [90, 114, 132] 7)
     22 (session "Model-based Clustering 2" "MBC" [] 6 [24, 55, 79] 8)
     23 (session "Data Science in Social Sciences 1" "DS-SS" [] 6 [33, 91, 153] 9)
     24 (session "Machine Learning 2" "ML" [] 6 [11, 172, 189] 10)
     25 (session "Classification of Time Series" 3 [] 7 [81, 149, 167] 1)     
     26 (session "Benchmarking Challenge" 3 [] 7 [264, 265, 268, 273] 2)
     27 (session "Dimension Reduction" "DR" [] 8 [71, 102, 214, 219] 1)
     28 (session "Functional Data Analysis 3" "FDA" [] 8 [21, 47, 152, 177] 2)
     29 (session "Time Series" "TS" [] 8 [7, 109, 164, 258] 3) 
     30 (session "Supervised Classification - Trees" "SCT" [] 8 [82, 210, 239, 242] 4)
     31 (session "Data Science in Health Sciences" "DS-HS" [] 8 [31, 125, 254, 255] 5)
     32 (session "Clustering 3" "CL" [] 8 [34, 37, 41] 6)
     33 (session "Compositional Data Analysis 1" "CoDA" [] 8 [14, 44, 201] 7)
     34 (session "Model-based Clustering 3" "MBC" [] 8 [28, 32, 257] 8)
     35 (session "Text Mining" "TM" [] 8 [30, 48, 168, 171] 9)
     36 (session "Symbolic Data Analysis 3" "SDA" [] 9 [107, 119, 209] 1)
     37 (session "Functional Data Analysis 4" "FDA" [] 9 [78, 155, 212] 2)
     38 (session "Optimization in Classification and Clustering 1" "OCC" [] 9 [77, 127, 231] 3)
     39 (session "50 Years of Biplots 2" "BIP" [] 9 [96, 145, 222] 4)
     40 (session "Data Science in Economics and Finance 1" "DS-EF" [] 9 [121, 139, 185] 5)
     41 (session "Clustering 4" "CL" [] 9 [29, 192, 221] 6)
     42 (session "Compositional Data Analysis 2" "CoDA" [] 9 [170, 174] 7)
     43 (session "Model-based Clustering 4" "MBC" [] 9 [19, 123, 204] 8)
     44 (session "Data Science in Social Sciences 2" "DS-SS" [] 9 [49, 113, 180] 9)
     45 (session "Machine Learning 3" "ML" [] 9 [2, 112, 270] 10)
     46 (session "Poster Session 1" 4 [] 10 [] 1)
     47 (session "Awards" 2 [] 11 [] 1)
     48 (session "Keynote Charles Bouveyron" 2 [] 12 [272] 1)
     49 (session "COVID Data Analysis" 3 [] 13 [206, 260, 262] 1)
     50 (session "Categorical Data Analysis and Visualization" 3 [] 13 [100, 117, 151] 2)
     51 (session "Presidential Address Angela Montanari" 2 [] 14 [284] 1)
     52 (session "Symbolic Data Analysis 4" "SDA" [] 15 [108, 147, 186] 1)
     53 (session "Functional Data Analysis 5" "FDA" [] 15 [43, 74, 134] 2)
     54 (session "Interpretable Machine Learning" "IML" [] 15 [190, 193, 250] 3)
     55 (session "50 Years of Biplots 3" "BIP" [] 15 [83, 103, 251] 4)
     56 (session "Optimization in Classification and Clustering 2" "OCC" [] 15 [15, 143, 188] 5)
     57 (session "Clustering 5" "CL" [] 15 [64, 95, 225] 6)
     58 (session "Robust Methods 3" "RM" [] 15 [5, 208, 217] 7)
     59 (session "Model-based Clustering 5" "MBC" [] 15 [26, 93, 198] 8)
     60 (session "Spatial-Temporal Data Analysis 1" "STDA" [] 15 [122, 181, 267] 9)
     61 (session  "SPE" "SPE" [] 15 [128, 130, 211] 10)
     62 (session "Keynote Genevera Allen" 2 [] 16 [271] 1)
     63 (session "Poster Session 2" 4 [] 17 [] 1)
     64 (session "Data Science for Business and Marketing" "DS-BM" [] 18 [39, 51, 159, 163] 1)
     65 (session "Functional Data Analysis 6" "FDA" [] 18 [65, 169, 202, 253, 280] 2)
     66 (session "Social Network Analysis 2" "SNA" [] 18 [36, 224, 243, 245] 3)
     67 (session "Data Science in Education" "DS-E" [] 18 [156, 241, 247, 248] 4)
     68 (session "Statistical Learning and Data Mining 3" "SLDM" [] 18 [72, 87, 89, 146] 5)
     69 (session "Clustering 6" "CL" [] 18 [16, 17, 23] 6)
     70 (session "Robust Methods 4" "RM" [] 18 [42, 94, 116, 223] 7)
     71 (session "Model-based Clustering 6" "MBC" [] 18 [8, 12, 110] 8)
     72 (session "Time Series Classification" "TSC" [] 18 [1, 22, 118, 227] 9)
     73 (session "IFCS Council meeting" 5 [] 19 [] 1)
     74 (session "Dimension Reduction" 3 [] 20 [200, 203, 220] 1)
     75 (session "Explainable Machine Learning" 3 [] 20 [259, 274, 279] 2)
     76 (session "Dimension Reduction & Clustering" "DR+Cl" [] 21 [63, 66, 166] 1)
     77 (session "Functional Data Analysis 7" "FDA" [] 21 [92, 124, 263] 2)
     78 (session "Supervised Classification 2" "SC" [] 21 [104, 115, 175] 3)
     79 (session "Model-based Clustering for Time Series" "MBC-TS" [] 21 [70, 136, 179] 4)
     80 (session "Optimization in Classification and Clustering 3" "OCC" [] 21 [182, 199, 276] 5)
     81 (session "Data Science in Economics and Finance 2" "DS-EF" [] 21 [35, 162, 197] 6)
     82 (session "Data Science in Biology" "DS-B" [] 21 [111, 161, 238] 7)
     83 (session "Spatial-Temporal Data Analysis 2" "STDA" [] 21 [69, 120, 142] 9)
     84 (session "Machine Learning 4" "ML" [] 21 [40, 191, 196] 10)
     86 (session "Keynote João Gama" 2 [] 22 [62] 1)
     87 (session "Closing Session" 2 [] 23 [] 1)
     }))

(def timeslots
  (reduce (fn [acc [id s]] (update-in acc [(:timeslot s) :sessions] #(conj % id))) timeslots sessions))

(def streams
  (reduce (fn [acc [id s]] (println s acc) (update-in acc [(:stream s) :sessions] #(conj % id))) streams sessions))

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
  (reduce #(update %1 %2 (fn [s] (conj s (:session a)))) acc (:authors a)))

(def user-sessions
  (reduce add-session-to-users {} abstracts))

(defn create-user [uname]
  (let [[firstname lastname] (s/split uname #" " 2)
        sessions (user-sessions (get user->id uname))]
    (into {} (User. firstname lastname sessions))))

(def users
  (zipmap (map inc (range)) (map create-user user-names)))

(def papers (zipmap (map :id abstracts) abstracts))

(defrecord Program [timeslots streams sessions papers keywords users])

(def program (Program. timeslots streams sessions papers keywords users))

(spit "resources/public/ifcs2022.edn" (into {} program))

(comment
  (filter #(nil? (:title (second %))) papers)
  (update {:k 2} :k inc)
  (process-abstract [282 1])
  (do (dorun (map process-abstract (range 1 285))) nil)
  (get user-sessions 1)
  )

