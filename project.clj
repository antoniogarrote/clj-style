(defproject clj-style "0.1.0-SNAPSHOT"
  :description "Front-end for different Clojure static analyzers"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [jonase/kibit "0.1.2"]
                 [jonase/eastwood "0.2.1"]
                 [lein-cljfmt "0.3.0"]]
  :eval-in-leiningen true)
