(ns move-anime.overrides)

(def overrides (read-string (slurp "overrides.edn")))


; Sometimes titles have to be overridden for Plex.
(def titles 
  (:titles overrides))



; When a season isn't season 1, override here by KW-ized title.
(def seasons
  (:seasons overrides))
