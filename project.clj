(defproject move-anime "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.4.0"]
                 [org.clojure/data.zip "0.1.2"]
                 [clj-http "3.7.0"]
                 [me.raynes/fs "1.4.6"]]
  :main ^:skip-aot move-anime.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
