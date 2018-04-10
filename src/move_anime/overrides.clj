(ns move-anime.overrides)

(def overrides
  (let [lines (with-open [rdr (clojure.java.io/reader "overrides.edn")]
                (reduce conj [] (line-seq rdr)))]
    (read-string (apply str lines))))

; Sometimes titles have to be overridden for Plex.
(def titles 
  (:titles overrides))



; When a season isn't season 1, override here by KW-ized title.
(def seasons
  (:seasons overrides))
