(ns leiningen.style.eastwood
  (:require [eastwood.lint :refer [eastwood]]
            [leiningen.style.errors :refer [make-error]]
            [leiningen.style.utils :refer [colorize print-progress]]))

(def warnings (atom []))

(defn warning->error [{:keys [msg file column line linter]}]
  (make-error file :eastwood linter (colorize msg :red)))

(defn check [project & args]
  (let [paths (filter some? (concat
                             (:source-paths project)
                             (:test-paths project)))
        results (promise)]
    (eastwood {;:dirs paths
               :source-paths (:source-paths project)
               :test-paths (:test-paths project)
               :callback (fn [message]
                           (condp = (:kind message)
                             :lint-warning (do
                                             (print-progress false)
                                             (swap! warnings conj (warning->error (:warn-data message))))
                             :note (print-progress true)
                             identity))})
    @warnings))
