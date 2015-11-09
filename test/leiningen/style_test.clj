(ns leiningen.style-test
  (:require [clojure.test :refer :all]
            [leiningen.style.check :refer :all]))

(def project-errors {:source-paths [(str (System/getProperty "user.dir")
                                         "/data/errors")]
                     :root (System/getProperty "user.dir")
                     :test-paths []})

(def project-correct (assoc project-errors :source-paths [(str (System/getProperty "user.dir")
                                                               "/data/correct")]))

(deftest test-cljfmt
  (let [results (check project-errors :cljfmt [])]
    (is (= (count results) 1)))
  (let [results (check project-correct :cljfmt [])]
    (is (empty? results))))

(deftest test-kibit
  (let [results (check project-errors :kibit [])]
    (is (= (count results) 1)))
  (let [results (check project-correct :kibit [])]
    (is (empty? results))))

; (deftest test-eastwood
;   (let [results (check project-errors :eastwood [])]
;     (is (= (count results) 1)))
;   (let [results (check project-correct :eastwood [])]
;     (is (empty? results))))
