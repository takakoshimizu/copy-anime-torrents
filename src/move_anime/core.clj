(ns move-anime.core
  (:use clojure.data.zip.xml)
  (:require [move-anime.feed :as feed]
            [move-anime.util :as util]
            [me.raynes.fs    :as fs])
  (:gen-class))


(def feed-url "http://www.shanaproject.com/feeds/user/44751/")


(def cur-feed (feed/parse-feed feed-url {:query-params {"count" "50"}}))


(defn determine-filename
  "Determines the Plex-proper filename for a given download filename."
  [fn feed]
  (let [item    (feed/xml->item feed fn)
        ext     (util/ext fn)
        title   (feed/item->title item)
        season  (util/season-for-title title)
        episode (util/episode-for-fn fn title)]
    (str title " - " (util/se-notation season episode) "." ext)))


(defn move-file
  "Moves a file to its new location."
  [filename origin-path output-path]
  (try
    (do 
      (println (str "Copying " filename " to " output-path))
      (util/copy-file origin-path output-path))
    (catch Exception e
      (do 
        (println (str "Unable to move file " filename))
        (println (str "Caught exception: " (.getMessage e)))))))


(defn move-episode
  "Moves a single episode."
  [filename origin-path output-path]
  (let [new-filename (determine-filename filename cur-feed)
        title        (feed/item->title (feed/xml->item cur-feed filename))
        new-path     (str output-path "/TV Shows/" title "/" new-filename)]
    (move-file filename origin-path new-path)))


(defn move-movie
  "Moves a movie file."
  [filename origin-path output-path]
  (let [new-path (str output-path "/Movies/" filename)]
    (move-file filename origin-path new-path)))


(defn -main
  [& args]
  (let [filename     (nth args 0)
        origin-path  (nth args 1)
        output-path  (nth args 2)
        xml-item     (feed/xml->item cur-feed filename)]
    (println (str "Filename: " filename))
    (println (str "Origin: " origin-path))
    (println (str xml-item))
    (if xml-item
      (move-episode filename origin-path output-path)
      (move-movie filename origin-path output-path))))
