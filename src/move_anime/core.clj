(ns move-anime.core
  (:use clojure.data.zip.xml)
  (:require [clojure.string  :as string]
            [clojure.xml     :as xml]
            [clojure.zip     :as zip]
            [clojure.java.io :as io]
            [clj-http.client :as client])
  (:gen-class))

(def season-overrides
  {:cardcaptor-sakura--clear-card-hen 4})

(def cur-feed
  (-> (client/get "http://www.shanaproject.com/feeds/user/44751/" {:query-params {"count" "50"}})
    (:body)
    (.getBytes)
    (java.io.ByteArrayInputStream.)
    (xml/parse)))

(defn feed-item-desc [item]
  (-> item :content (nth 2) :content first))

(defn xml->item [filename]
  (let [xml-zip (zip/xml-zip cur-feed)
        items   (xml-> xml-zip :channel :item)]
    (->>
      items
      (filter #(= (feed-item-desc (first %)) filename))
      (first)
      (first))))

(defn item->folder [item]
  (-> item :content (nth 5) :content first))
      
(defn item->title [item]
  (let [folder (item->folder item)]
    (-> folder (string/split #"/") (nth 1))))

(defn title->kw [title]
  (let [replace-nonchar (fn [str] (string/replace str #"[^\w]" "-"))]
    (-> title
      (replace-nonchar)
      (string/lower-case)
      (keyword))))

(defn season-for-title [title]
  (let [kw-title (title->kw title)
        season   (get season-overrides kw-title)]
    (or season 1)))

(defn episode-for-fn [fn title]
  (let [titleless-fn (string/replace fn title "")
        clean-fn (string/trim (string/replace titleless-fn #"\[[^\]]+\]" ""))]
    (read-string (string/trim (string/replace clean-fn #"[^\d]" "")))))

(defn leading-zero [x]
  (if (> x 9)
    (str x)
    (str "0" x)))

(defn se-notation [season episode]
  (str "s" (leading-zero season) "e" (leading-zero episode)))

(defn extension [fn]
  (->
    fn
    (string/split #"\.")
    (last)))

(defn determine-filename [fn]
  (let [item    (xml->item fn)
        ext     (extension fn)
        title   (item->title item)
        season  (season-for-title title)
        episode (episode-for-fn fn title)]
    (str title " - " (se-notation season episode) "." ext)))

(defn copy-file [from to]
  (io/copy (io/file from) (io/file to)))

(defn -main
  [& args]
  (let [filename     (nth args 0)
        origin-path  (nth args 1)
        output-path  (nth args 2)
        new-filename (determine-filename filename)
        title        (item->title (xml->item filename))
        new-path     (str output-path "/TV Shows" title "/" new-filename)]
    (do 
      (println (str "Copying " filename " to " new-path))
      (copy-file origin-path new-path))))
