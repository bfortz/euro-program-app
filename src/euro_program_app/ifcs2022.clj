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
   "SPE" (stream "Classiﬁcation Applied to Biological Sciences") 
   "SLDM" (stream "Statistical Learning and Data Mining") 
   "SEM" (stream "Statistics and Econometric Methods") 
   "SC" (stream "Supervised Classification") 
   "SCT" (stream "Supervised Classification - Trees") 
   "S-ML" (stream "Supervised Machine Learning") 
   "SDA" (stream "Symbolic Data Analysis") 
   "TM" (stream "Text Mining") 
   "TS" (stream "Time Series") 
   "TSC" (stream "Time Series Classification")})

(defrecord Session [name stream chairs timeslot papers track acronym])
(defn session [n s c ts p tr a] (into {} (Session. n s c ts p tr a)))

(def sessions
  (into 
    (sorted-map) 
    (zipmap 
      (map inc (range)) 
      (sort-by #(+ (* 1000 (:timeslot %)) (:track %)) 
               (map #(apply session %) 
                    [["Analysis of Data Streams" 1 [] 1 [282] 1 "T1"]
                     ["Categorical Data Analysis and Visualization" 1 [] 2 [283] 1 "T2"]
                     ["Opening Session" 2 [] 3 [] 0 "OS"]
                     ["Keynote Dianne Cook" 2 [1156856374] 4 [256] 0 "K1"]
                     ["Symbolic Data Analysis 1" "SDA" [1755792008] 5 [157, 187, 275] 5 "SDA1"]
                     ["Functional Data Analysis 1" "FDA" [-1468429205] 5 [47, 85, 235] 2 "FDA1"]
                     ["Social Network Analysis 1" "SNA" [227631459] 5 [13, 38, 195] 3 "SNA1"]
                     ["50 Years of Biplots 1" "BIP" [-860309800] 5 [75, 158, 240] 4 "BIP1"]
                     ["Statistical Learning and Data Mining 1" "SLDM" [-1928671837] 5 [99, 105, 144] 8 "SLDM1"]
                     ["Clustering 1" "CL" [951086089] 5 [50, 160, 237] 6 "CL1"]
                     ["Robust Methods 1" "RM" [-131426338] 5 [138, 148, 232] 7 "RM1"]
                     ["Model-based Clustering 1" "MBC" [1348059205] 5 [6, 18, 76] 1 "MBC1"]
                     ["Supervised Machine Learning" "S-ML" [1518611897] 5 [40, 98] 9 "S-ML"]
                     ["Machine Learning 1" "ML" [511666866] 5 [129, 154, 173] 10 "ML1"]
                     ["Symbolic Data Analysis 2" "SDA" [1755792008] 6 [56, 59, 86] 5 "SDA2"]
                     ["Functional Data Analysis 2" "FDA" [-1854364483] 6 [84, 135, 226] 2 "FDA2"]
                     ["Supervised Classification 1" "SC" [1833189783] 6 [126, 205, 230] 4 "SC1"]
                     ["Statistics and Econometric Methods" "SEM" [636652566] 6 [61, 97, 141] 9 "SEM"]
                     ["Statistical Learning and Data Mining 2" "SLDM" [-1820902260] 6 [27, 131, 133] 8 "SLDM2"]
                     ["Clustering 2" "CL" [-2076635564] 6 [10, 25, 184] 6 "CL2"]
                     ["Robust Methods 2" "RM" [-131426338] 6 [90, 114, 132] 7 "RM2"]
                     ["Model-based Clustering 2" "MBC" [-1971974347] 6 [24, 55, 79] 1 "MBC2"]
                     ["Data Science in Social Sciences 1" "DS-SS" [34861845] 6 [33, 91, 153] 3 "DS-SS1"]
                     ["Machine Learning 2" "ML" [658595747] 6 [11, 172, 189] 10 "ML2"]
                     ["Classification of Time Series" 3 [-1875806151] 7 [81, 149, 167] 1 "INV1"]     
                     ["Benchmarking Challenge" 3 [1244692504] 7 [264, 265, 268, 273] 10 "BC"]
                     ["Dimension Reduction" "DR" [-1304129030] 8 [71, 102, 214, 219] 10 "DR"]
                     ["Functional Data Analysis 3" "FDA" [644632721] 8 [21, 47, 152, 177] 2 "FDA3"]
                     ["Time Series" "TS" [1034765172] 8 [7, 109, 164, 258] 5 "TS"] 
                     ["Supervised Classification - Trees" "SCT" [2077906742] 8 [82, 210, 239, 242] 4 "SCT"]
                     ["Data Science in Health Sciences" "DS-HS" [535104989] 8 [31, 125, 254, 255] 8 "DS-HS"]
                     ["Clustering 3" "CL" [1244692504] 8 [34, 37, 41] 6 "CL3"]
                     ["Compositional Data Analysis 1" "CoDA" [1400430920] 8 [14, 44, 201] 9 "CoDA1"]
                     ["Model-based Clustering 3" "MBC" [-1770841430] 8 [28, 257, 198, 32] 1 "MBC3"]
                     ["Text Mining" "TM" [-894655016] 8 [30, 48, 168, 171] 3 "TM"]
                     ["Symbolic Data Analysis 3" "SDA" [1332227302] 9 [107, 119, 209] 5 "SDA3"]
                     ["Functional Data Analysis 4" "FDA" [-774412915] 9 [78, 155, 212] 2 "FDA4"]
                     ["Optimization in Classification and Clustering 1" "OCC" [2039042684] 9 [77, 127, 231] 8 "OCC1"]
                     ["50 Years of Biplots 2" "BIP" [1312906308] 9 [96, 145, 222] 4 "BIP2"]
                     ["Data Science in Economics and Finance 1" "DS-EF" [-1484986528] 9 [121, 139, 185] 7 "DS-EF1"]
                     ["Clustering 4" "CL" [-990593308] 9 [29, 192, 221] 6 "CL4"]
                     ["Compositional Data Analysis 2" "CoDA" [-1172329060] 9 [170, 174] 9 "CoDA2"]
                     ["Model-based Clustering 4" "MBC" [719400037] 9 [19, 123, 204] 1 "MBC4"]
                     ["Data Science in Social Sciences 2" "DS-SS" [358996414] 9 [49, 113, 180] 3 "DS-SS2"]
                     ["Machine Learning 3" "ML" [1290924990] 9 [2, 112, 270] 10 "ML3"]
                     ["Poster Session 1" 4 [] 10 [45, 54, 57, 58, 67, 73, 88, 106, 137, 140, 176, 178, 183, 218] 12 "PS1"]
                     ["Benchmarking Challenge Award" 2 [1833189783] 11 [] 0 "BCA"]
                     ["IFCS Awards" 2 [-1633765519] 12 [] 0 "AS"]
                     ["Keynote Edwin Diday" 2 [-1633765519] 13 [281] 0 "KL"]
                     ["Keynote Charles Bouveyron" 2 [211672853] 14 [272] 0 "K2"]
                     ["COVID Data Analysis" 3 [-180899592] 15 [206, 260, 262] 1 "INV2"]
                     ["Categorical Data Analysis and Visualization" 3 [-1746059320] 15 [100, 117, 151] 10 "INV3"]
                     ["Presidential Address Angela Montanari" 2 [499089750] 16 [284] 0 "KL"]
                     ["Symbolic Data Analysis 4" "SDA" [966350613] 17 [108, 147, 186] 5 "SDA4"]
                     ["Functional Data Analysis 5" "FDA" [1216026640] 17 [43, 74, 134] 2 "FDA5"]
                     ["Interpretable Machine Learning" "IML" [1183902538] 17 [190, 193, 250] 10 "IML"]
                     ["50 Years of Biplots 3" "BIP" [-1540630165] 17 [83, 103, 251] 4 "BIP3"]
                     ["Optimization in Classification and Clustering 2" "OCC" [231349039] 17 [15, 143, 188] 8 "OCC2"]
                     ["Clustering 5" "CL" [99034026] 17 [64, 95, 225] 6 "CL5"]
                     ["Robust Methods 3" "RM" [-1326682421] 17 [5, 208, 217] 7 "RM3"]
                     ["Model-based Clustering 5" "MBC" [-544228136] 17 [26, 93] 1 "MBC5"]
                     ["Spatial-Temporal Data Analysis 1" "STDA" [1973313263] 17 [122, 181, 267] 3 "STDA1"]
                     ["Classification Applied to Biological Sciences" "SPE" [843320350, -1046537934] 17 [128, 130, 211] 9 "SPE"]
                     ["Keynote Genevera Allen" 2 [459379652] 18 [271] 0 "K3"]
                     ["Poster Session 2" 4 [] 19 [68, 101, 194, 213, 215, 216, 233, 234, 244, 249, 252, 269, 277, 278] 12 "PS2"]
                     ["Data Science for Business and Marketing" "DS-BM" [-1858407424] 20 [39, 51, 159, 163] 4 "DS-BM"]
                     ["Functional Data Analysis 6" "FDA" [2000451620] 20 [65, 169, 202, 253, 280] 2 "FDA6"]
                     ["Social Network Analysis 2" "SNA" [331192588] 20 [36, 224, 243, 245] 3 "SNA2"]
                     ["Data Science in Education" "DS-E" [1130331529] 20 [156, 241, 247, 248] 9 "DS-E"]
                     ["Statistical Learning and Data Mining 3" "SLDM" [-1820902260] 20 [72, 87, 89, 146] 8 "SLDM3"]
                     ["Clustering 6" "CL" [476799132] 20 [16, 17, 23] 6 "CL6"]
                     ["Robust Methods 4" "RM" [-1326682421] 20 [42, 94, 116, 223] 7 "RM4"]
                     ["Model-based Clustering 6" "MBC" [-352737693] 20 [8, 12, 110] 1 "MBC6"]
                     ["Time Series Classification" "TSC" [1111807754] 20 [1, 22, 118, 227] 10 "TSC"]
                     ["IFCS Council meeting" 5 [] 21 [] 11 "CM"]
                     ["Dimension Reduction" 3 [-142218481] 22 [200, 203, 220] 1 "INV4"]
                     ["Explainable Machine Learning" 3 [-993048863] 22 [259, 274, 279] 10 "INV5"]
                     ["Dimension Reduction & Clustering" "DR+Cl" [-954358545] 23 [63, 66, 166] 6 "DR+Cl"]
                     ["Functional Data Analysis 7" "FDA" [-1841188346] 23 [92, 124, 263] 2 "FDA7"]
                     ["Supervised Classification 2" "SC" [-1481632887] 23 [104, 115, 175] 4 "SC2"]
                     ["Model-based Clustering for Time Series" "MBC-TS" [-75708338] 23 [70, 136, 179] 4 "MBC-TS"]
                     ["Optimization in Classification and Clustering 3" "OCC" [291261695] 23 [182, 199, 276] 8 "OCC3"]
                     ["Data Science in Economics and Finance 2" "DS-EF" [-1671994733] 23 [35, 162, 197] 7 "DS-EF2"]
                     ["Data Science in Biology" "DS-B" [998975565] 23 [111, 161, 238] 9 "DS-B"]
                     ["Spatial-Temporal Data Analysis 2" "STDA" [1973313263] 23 [69, 120, 142] 3 "STDA2"]
                     ["Machine Learning 4" "ML" [198167896] 23 [4, 191, 196] 10 "ML4"]
                     ["Keynote João Gama" 2 [591521497] 24 [62] 0 "K4"]
                     ["Closing Session" 2 [] 25 [] 0 "CLS"]])))))

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
          authors (s/split authors #"[,\r\n ]+and ")
          authors (map #(s/split % #",") authors)
          authors (flatten authors)
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
  (map #(update %1 :keywords (fn [kws] (map (fn [k] (kw->id k)) kws))) abstracts))

(defn add-session-to-keywords [acc a]
  (reduce #(update %1 %2 (fn [s] (sort (conj s (:session a))))) acc (:keywords a)))

(def kw-sessions
  (reduce add-session-to-keywords {} abstracts))

(def keywords
  (zipmap (map inc (range)) (map #(into {} (Keyword. % (kw-sessions (kw->id %)))) keyword-names)))

(defrecord User [firstname lastname sessions])

(def user-names 
  (sort
    (reduce 
      #(conj %1 %2)
      (reduce #(union %1 (set (:authors %2))) #{} abstracts)
      ["Nema Dean" "Matthijs Warrens" "Herbert Lee" "Charles Taylor" "Krzysztof Jajuga" "Hien Nguyen" "Laura Palagi" "José G. Dias" "Rebecca Nugent" "Pedro Duarte Silva"])))

(def user->id (zipmap user-names (map hash user-names)))

(def abstracts
  (map #(update %1 :authors (fn [authors] (map (fn [k] (user->id k)) authors))) abstracts))

(defn add-session-to-users [acc a]
  (reduce #(update %1 %2 (fn [s] (sort (distinct (conj s (:session a)))))) acc (:authors a)))

(defn add-session-chair-to-users [acc [id s]]
  (reduce #(update %1 %2 (fn [s] (distinct (conj s id)))) acc (:chairs s)))

(def user-chairs
  (reduce add-session-chair-to-users {} sessions))

(def user-sessions
  (reduce add-session-to-users user-chairs abstracts))

(defn split-uname [uname]
  (if (= '\" (first uname))
    (s/split (subs uname 1) #"\" " 2)
    (let [[f l] (s/split uname #" " 2)]
      (if (= '\. (second l))
        (let [[f2 l2] (s/split l #" " 2)]
          [(str f " " f2) l2])
        [f l]))))

(defn create-user [uname]
  (let [[firstname lastname] (split-uname uname) 
        sessions (user-sessions (user->id uname))]
    (into {} (User. firstname lastname sessions))))

(def users
  (zipmap (map hash user-names) (map create-user user-names)))

(def papers (zipmap (map :id abstracts) abstracts))

(defrecord Room [room])

(def rooms 
  (zipmap (range) (map #(into {} (Room. %)) ["Grand Hall" "118" "113" "213" "260" "115" "135" "159" "211" "215" "218" "Council Room" "Main Hall, Ground Floor"])))

(defrecord Program [timeslots streams sessions papers keywords users rooms])

(def program (Program. timeslots streams sessions papers keywords users rooms))

(spit "resources/public/ifcs2022.edn" (into {} program))

(comment
  (filter #(nil? (:title (second %))) papers)
  (update {:k 2} :k inc)
  (process-abstract [282 1])
  (do (dorun (map process-abstract (range 1 285))) nil)
  (users 1001)
  )

