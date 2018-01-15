(ns move-anime.util
  (:require [move-anime.overrides :as overrides]
            [clojure.string :as string]
            [clojure.java.io :as io]))


(defn- replace-nonchar
  "Replaces all characters in str that are non-words with a hyphen."
  [str]
  (string/replace str #"[^\w]" "-"))


(defn title->kw
  "Converts a fully-stringed title into a lowercase keyword."
  [title]
  (-> title
    (replace-nonchar)
    (string/lower-case)
    (keyword)))


(defn season-for-title 
  "Retrieves the season for this title, if it exists in the overrides. 
  Otherwise, 1 fits most use cases."
  [title]
  (let [kw-title (title->kw title)
        season   (get overrides/seasons kw-title)]
    (or season 1)))


(defn clean-title
  "Sometimes needs to override titles for Plex scanners."
  [title]
  (let [kw-title    (title->kw title)
        clean-title (get overrides/titles kw-title)]
    (or clean-title title)))


(defn episode-for-fn 
  "Strips out all title and metadata from a filename to gather the episode number."
  [fn title]
  (let [titleless-fn (string/replace fn title "")
        clean-fn (string/trim (string/replace titleless-fn #"\[[^\]]+\]" ""))]
    (read-string (string/trim (string/replace clean-fn #"[^\d]" "")))))


(defn leading-zero 
  "If x is less than 9, add a leading zero. Otherwise, unchanged. As string."
  [x]
  (if (> x 9)
    (str x)
    (str "0" x)))


(defn se-notation 
  "Returns the s00e00 notation expected by Plex."
  [season episode]
  (str "s" (leading-zero season) "e" (leading-zero episode)))


(defn ext
  "Returns the extension for a given filename"
  [fn]
  (->
    fn
    (string/split #"\.")
    (last)))


(defn copy-file 
  "Copies a file. Creates parent directories for new location prior to copy."
  [from to]
  (do
    (io/make-parents to)
    (io/copy (io/file from) (io/file to))))
