(ns leiningen.style
  (:require [clojure.string :as str]
            [leiningen.core.main :as main]
            [leiningen.check :refer [check]]
            [leiningen.eastwood :refer [eastwood]]
            [leiningen.style.check :as c]
            [leiningen.style.fix :as f]
            [leiningen.cloverage :as cloverage]
            [leiningen.style.utils :refer [print-help]]
            [leiningen.style.printing :refer [colorize]]))

(defn display-error [{:keys [file source description text]}]
  (main/warn (str (colorize "* Error: " :yellow) (colorize description :cyan)))
  (main/warn (str (colorize "- File: " :yellow) file))
  (main/warn (str (colorize "- Source: " :yellow) source))
  (main/warn (str (colorize "- Description:\n" :yellow) text "\n")))

(defn apply-fix [{:keys [file error fix]}]
  (display-error error)
  (colorize (str "* Fixing " file) :green)
  (fix))

(defn process-results [errors]
  (println "\n")
  (if (empty? errors)
    (main/info (colorize "No errors found" :green))
    (do (doseq [error errors] (display-error error))
        (main/abort (colorize (str "\n-------\n" (count errors) " errors(s) found") :red)))))

(defn process-fixes [fixes]
  (println "\n")
  (if (empty? fixes)
    (main/info (colorize "No errors to fix found" :green))
    (do (doseq [fix fixes] (apply-fix fix))
        (main/info (colorize (str "\n-------\n" (count fixes) " errors(s) fixed") :green)))))

(defn run-coverage [project]
  (cloverage/cloverage project))

(defn style
  "Run multiple style checks on a Clojure project"
  ([project] (print-help))
  ([project command]
   (if (= (keyword command) :coverage)
     (run-coverage project)
     (style project command :all)))
  ([project command library & args]
   (condp = (keyword command)
     :check (process-results
             (c/check project (keyword library) args))
     :fix   (process-fixes (f/fix project (keyword library) args))
     :coverage (run-coverage project)
     (print-help))))
