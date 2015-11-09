(ns leiningen.style.kibit
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [leiningen.core.main :as main]
            [leiningen.style.errors :refer [make-error]]
            [leiningen.core.eval :refer [eval-in-project]]
            [clojure.tools.namespace.find :refer [find-namespaces]]
            [kibit.rules :refer [all-rules]]
            [kibit.check :refer [check-file]]
            [kibit.driver :refer [find-clojure-sources-in-dir]]
            [leiningen.cljfmt.diff :as diff]
            [clojure.pprint :as pp]
            [leiningen.style.utils :refer [colorize print-progress]])
  (:import [java.io File]
           [difflib DiffUtils Delta$TYPE]
           [java.io StringWriter]))

(defn run [source-paths rules]
  (let [source-files (mapcat #(-> % io/file find-clojure-sources-in-dir) source-paths)]
    (mapcat (fn [file] (try (map #(assoc % :file file) (let [res (check-file file :reporter identity  :rules (or rules all-rules))]
                                                         (print-progress (empty? res))
                                                         res))
                            (catch Exception e
                              (println "Check failed -- skipping rest of file")
                              (println (.getMessage e)))))
            source-files)))

(defn pprint-code [form]
  (let [string-writer (StringWriter.)]
    (pp/write form
              :dispatch pp/code-dispatch
              :stream string-writer
              :pretty true)
    (->> (str string-writer)
         string/split-lines
         (map #(str "  " %))
         (string/join "\n"))))

(defn- result->error [{:keys [file line column expr alt] :as result}]
  (let [file-path (str file)]
    (make-error
     file-path
     :kibit
     "semantic"
     (str "At line " line " column " column " consider using \n"
          (colorize (pprint-code alt) :green)
          "\ninstead of\n"
          (colorize (pprint-code expr) :red)))))

(defn check
  [project & args]
  (let [paths (filter some? (concat
                             (:source-paths project)
                             (:test-paths project)))
        rules (get-in project [:kibit :rules])
        rules (when rules (apply concat (vals rules)))]
    (let [results (run paths rules)]
      (map result->error (doall results)))))
