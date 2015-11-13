(ns leiningen.style.eastwood
  (:require [eastwood.lint :refer [eastwood]]
            [leiningen.core.eval :refer [eval-in-project]]
            [leiningen.core.project :refer [merge-profiles]]
            [leiningen.style.errors :refer [make-error]]
            [leiningen.style.printing :refer [colorize]]))

(defn warning->error [{:keys [msg column line linter uri-or-filename file uri]}]
  (make-error (str (or file uri-or-filename uri) " line" line " column " column) :eastwood linter (colorize msg :red)))

(defn error->error [{:keys [msg opt]}]
  (make-error (-> opt :cwd (str)) :eastwood :error (colorize msg :red)))

(defn check [project & args]
  (let [paths (filter some? (concat
                             (:source-paths project)
                             (:test-paths project)))
        results (promise)
        project (merge-profiles project [{:dependencies [['jonase/eastwood "0.2.1"]]}])
        project (update-in project [:eval-in] (constantly :classloader))
        warnings (try (eval-in-project
                       project
                       `(let [ansi-colors#
                              {:reset  "[0m"
                               :red    "[031m"
                               :green  "[032m"
                               :cyan   "[036m"
                               :yellow "[33m"}
                              colorize# (fn [s# color#]
                                          (str \u001b (ansi-colors# color#) s# \u001b (ansi-colors# :reset)))
                              warnings# (atom [])]
                         (eastwood.lint/eastwood {;:dirs paths
                                                  :source-paths (:source-paths '~project)
                                                  :test-paths (:test-paths '~project)
                                                  :callback (fn [message#]
                                                              (condp = (:kind message#)
                                                                :lint-warning (do
                                                                                (print (colorize# "." :red))
                                                                                (swap! warnings# conj {:kind :warning
                                                                                                       :data (:warn-data message#)}))
                                                                :note (print (colorize# "." :green))
                                                                identity))})
                         (deref warnings#))
                       '(require 'eastwood.lint))
                      (catch Exception ex (print (colorize "!" :red)) []))]
    (map (fn [{:keys [kind data]}] (condp = kind
                                     :warning (warning->error data)
                                     :error (error->error data)))
         warnings)))
