(ns move-anime.util-test
  (:require [clojure.test    :refer :all]
            [move-anime.util :as util]))


; found this swanky macro online
(defmacro with-private-fns [[ns fns] & tests]
  "Refers private fns from ns and runs tests in context."
  `(let ~(reduce #(conj %1 %2 `(ns-resolve '~ns '~%2)) [] fns)
     ~@tests))


(with-private-fns [move-anime.util [replace-nonchar]]
  (deftest replace-nonchar
    (testing "Replaces all non-word characters with hyphens"
      (is (= "Cardcaptor-Sakura--Clear-Card-hen" (replace-nonchar "Cardcaptor Sakura: Clear Card hen"))))

    (testing "Leaves word characrters intact"
      (is (= "abcdefgHiJkLMNOPqrsTUVwXyZ" (replace-nonchar "abcdefgHiJkLMNOPqrsTUVwXyZ"))))))


(deftest title->kw
  (testing "Returns a lowercase, hyphenated, keyworded title"
    (is (= :cardcaptor-sakura--clear-card-hen (util/title->kw "Cardcaptor Sakura: Clear Card-hen")))
    (is (= :citrus (util/title->kw "Citrus")))
    (is (= :death-march-to-the-parallel-world-rhapsody (util/title->kw "Death March to the Parallel World Rhapsody"))))

  (testing "Does not explode when given an empty title"
    (is (= (keyword "") (util/title->kw ""))))

  (testing "Does not explode when given nil"
    (is (= (keyword "") (util/title->kw "")))))


(deftest season-for-title)
