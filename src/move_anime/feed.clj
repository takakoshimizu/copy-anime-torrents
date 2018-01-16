(ns move-anime.feed
  (:use clojure.data.zip.xml)
  (:require [move-anime.util  :as util]
            [clojure.string :as string]
            [clojure.xml      :as xml]
            [clojure.zip      :as zip]
            [clj-http.client  :as client]))


(defn parse-feed 
  "Parses an XML feed from a URL into an XML map."
  [feed-url query-params]
  (-> (client/get feed-url query-params)
    (:body)
    (.getBytes)
    (java.io.ByteArrayInputStream.)
    (xml/parse)))


(defn find-tag 
  "Finds a particular named tag in a list of XML siblings."
  [tags tag-name]
  (if-let [tag (keep #(when (= (:tag %) tag-name) %) tags)]
    (first tag)
    nil))


(defn item->desc
  "Takes in a feed XML item and returns the description field."
  [item]
  (-> 
    item :content 
    (find-tag :description) :content
    first))


(defn xml->item 
  "Finds a particular XML feed item by the filename (found in description)"
  [feed filename]
  (let [xml-zip (zip/xml-zip feed)
        items   (xml-> xml-zip :channel :item)]
    (try
      (->>
        items
        (filter #(= (item->desc (first %)) filename))
        first first)
      (catch Exception e 
        nil))))


(defn item->folder 
  "Retrieves the folder from an XML feed item."
  [item]
  (-> 
    item :content 
    (find-tag :folder) :content 
    first))


(defn item->title 
  "Retrieves the parsed title out of the folder definition."
  [item]
  (let [folder (item->folder item)]
    (-> 
      folder 
      (string/split #"/") 
      (nth 1)
      (util/clean-title))))
