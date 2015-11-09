(ns leiningen.style
  (:require [clojure.string :as str]
            [leiningen.core.main :as main]
            [leiningen.check :refer [check]]
            [leiningen.eastwood :refer [eastwood]]
            [leiningen.style.check :as c]
            [leiningen.style.fix :as f]
            [leiningen.style.errors :as errors]
            [leiningen.style.utils :refer [colorize print-help]]))

(defn process-results [errors]
  (println "\n")
  (if (empty? errors)
    (main/info (colorize "No errors found" :green))
    (do (doseq [error errors] (errors/display-error error))
        (main/abort (colorize (str "\n-------\n" (count errors) " errors(s) found") :red)))))

(defn process-fixes [fixes]
  (println "\n")
  (if (empty? fixes)
    (main/info (colorize "No errors to fix found" :green))
    (do (doseq [fix fixes] (errors/apply-fix fix))
        (main/info (colorize (str "\n-------\n" (count fixes) " errors(s) fixed") :green)))))

(defn style
  "Run multiple style checks on a Clojure project"
  ([project] (print-help))
  ([project command] (style project command :all))
  ([project command library & args]
   (condp = (keyword command)
     :check (process-results
             (c/check project (keyword library) args))
     :fix   (process-fixes (f/fix project (keyword library) args))
     (print-help))))
