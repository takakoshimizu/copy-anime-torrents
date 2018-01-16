(ns move-anime.feed-test
  (:require [clojure.test    :refer :all]
            [move-anime.feed :as feed]
            [clojure.xml     :as xml]))

(def test-feed 
  (-> 
    (slurp "example.xml")
    (.getBytes)
    (java.io.ByteArrayInputStream.)
    (xml/parse)))


(def test-item
  (feed/xml->item test-feed "[HorribleSubs] Citrus - 01 [720p].mkv"))


(def test-title-item
  (assoc-in test-item [:content 5 :content 0] "Winter/testing"))


(deftest find-tag
  (testing "Returns a whole tag."
    (let [item (feed/find-tag (:content test-item) :title)]
      (is (map? item))
      (is (= [:tag :attrs :content] (keys item)))))

  (testing "Retrieves the correct tag item."
    (let [item (feed/find-tag (:content test-item) :title)]
      (is (= :title (:tag item)))))

  (testing "Returns nil if the tag cannot be found."
    (let [item (feed/find-tag (:content test-item) :testing)]
      (is (nil? item)))))


(deftest xml->item
  (testing "Provides the correct item by filename."
    (let [item  (feed/xml->item test-feed "[HorribleSubs] Citrus - 01 [720p].mkv")
          title (-> item :content (feed/find-tag :title) :content first)]
      (is (= "Citrus - 1 [720p][HorribleSubs]" title))))
  
  (testing "Returns nil when filename is not found."
    (let [item (feed/xml->item test-feed "testtest")]
      (is (nil? item)))))


(deftest item->folder
  (testing "Returns the folder for a given item"
    (let [folder (feed/item->folder test-item)]
      (is (= "2018 Winter/Citrus" folder))))

  (testing "Returns nil if not a valid item."
     (let [folder (feed/item->folder {})]
       (is (nil? folder)))))


(deftest item->title
  (testing "Returns the title of a given item."
    (let [title (feed/item->title test-item)]
      (is (= "Citrus" title))))

  (testing "can override titles if necessary"
    (let [title (feed/item->title test-title-item)]
      (is (= "test override" title)))))
