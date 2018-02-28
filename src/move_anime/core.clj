(ns move-anime.core
  (:use clojure.data.zip.xml)
  (:require [clojure.tools.logging :as log]
            [move-anime.feed :as feed]
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
      (log/info (str "Copying " filename " to " output-path))
      (util/copy-file origin-path output-path))
    (catch Exception e
      (do 
        (log/error (str "Unable to move file " filename))
        (log/error (str "Caught exception: " (.getMessage e)))))))


(defn move-episode
  "Moves a single episode."
  [filename origin-path output-path]
  (let [new-filename (determine-filename filename cur-feed)
        item         (feed/xml->item cur-feed filename)
        title        (feed/item->title item)
        season       (feed/item->season item)
        new-path     (str output-path "/TV Shows/" season "/" title "/" new-filename)]
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
    (log/info (str "Filename: " filename))
    (log/info (str "Origin: " origin-path))
    (log/info (str xml-item))
    (if xml-item
      (move-episode filename origin-path output-path)
      (move-movie filename origin-path output-path))))
