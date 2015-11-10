(ns leiningen.style.cloverage
  (:use [cloverage source instrument debug report dependency])
  (:require [cloverage.coverage :as cloverage]
            [cloverage.report :as report]
            [bultitude.core :as blt]
            [leiningen.style.errors :refer [make-error]]
            [leiningen.style.utils :refer [colorize with-muted-output]]
            [clojure.set :as set]))

(defn ns-names-for-dirs [dirs]
  (map name (mapcat blt/namespaces-in-dir dirs)))

(defn run
  "Produce test coverage report for some namespaces"
  [& args]
  (let [[opts add-nses help] (cloverage/parse-args args)
        add-test-nses (:extra-test-ns opts)
        ns-regexs     (map re-pattern (:ns-regex opts))
        test-regexs   (map re-pattern (:test-ns-regex opts))
        exclude-regex (map re-pattern (:ns-exclude-regex opts))
        ns-path       (:src-ns-path opts)
        test-ns-path  (:test-ns-path opts)
        namespaces    (set/difference
                       (into #{}
                             (concat add-nses
                                     (cloverage/find-nses ns-path ns-regexs)))
                       (into #{} (cloverage/find-nses ns-path exclude-regex)))
        test-nses     (concat add-test-nses (cloverage/find-nses test-ns-path test-regexs))]
    (binding [*ns*      (find-ns 'cloverage.coverage)
              *debug*   false]
      (doseq [namespace (in-dependency-order (map symbol namespaces))]
        (binding [cloverage/*instrumented-ns* namespace]
          (instrument #'cloverage/track-coverage namespace))
        (cloverage/mark-loaded namespace))
      (let [test-result (when-not (empty? test-nses)
                          (let [test-syms (map symbol test-nses)]
                            (apply require (map symbol test-nses))
                            (apply clojure.test/run-tests (map symbol test-nses))))
            ;; sum up errors as in lein test
            errors      (when test-result
                          (reduce + ((juxt :error :fail) test-result)))
            exit-code   (cond
                          (not test-result) -1
                          (> errors 128)    -2
                          :else             errors)]
        (let [results (gather-stats (deref cloverage/*covered*))]
          (shutdown-agents)
          (report/total-stats results))))))

(defn check [project & args]
  (try (let [min-coverage (get-in project [:clj-style :min-coverage])
             source-namespaces (ns-names-for-dirs (:source-paths project))
             test-namespace    (ns-names-for-dirs (:test-paths project))
             args (concat (mapcat  #(list "-x" %) test-namespace)
                          args
                          source-namespaces)]
         (let [results (with-muted-output (apply run args))
               min-coverage (or min-coverage 90)
               percent-covered (:percent-lines-covered results)]
           (if (< percent-covered min-coverage)
             [(make-error ""
                          :cloverage
                          "test coverage"
                          (str "Total line coverage (" (colorize (str percent-covered "%") :red) ") not reaching minimum of " min-coverage "%"))]
             [])))
       (catch Exception ex (do (print (colorize "!" :red))[]))))
