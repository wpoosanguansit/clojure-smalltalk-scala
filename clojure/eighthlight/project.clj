(defproject eighthlight "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [matchure "0.10.1"]
                 [org.clojure/tools.cli "0.3.1"]]
  :main ^:skip-aot eighthlight.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
