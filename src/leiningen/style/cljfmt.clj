(ns leiningen.style.cljfmt
  (:require [leiningen.core.main :as main]
            [leiningen.style.errors :refer [make-error]]
            [leiningen.cljfmt :as cljfmt]
            [leiningen.style.utils :refer [print-progress]]))

(defn check
  ([project]
   (apply check project (cljfmt/format-paths project)))
  ([project path & paths]
   (let [files   (mapcat (partial cljfmt/find-files project) (cons path paths))
         invalid-files (remove #(let [result (cljfmt/valid-format? project %)]
                                  (print-progress result)
                                  result)
                               files)]
     (map #(let [file-path (cljfmt/project-path project %1)]
             (make-error file-path
                         :cljfmt
                         "syntax"
                         (str (cljfmt/format-diff project %1) "\n")))
          (doall invalid-files)))))

(defn fix
  ([project]
   (apply fix project (cljfmt/format-paths project)))
  ([project path & paths]
   (let [files   (mapcat (partial cljfmt/find-files project) (cons path paths))
         invalid-files (remove #(let [result (cljfmt/valid-format? project %)]
                                  (print-progress result)
                                  result)
                               files)]
     (map
      (fn [f] (let [file-path (cljfmt/project-path project f)]
                {:error
                 (make-error file-path
                             :cljfmt
                             "syntax"
                             (str (cljfmt/format-diff project f) "\n"))
                 :fix #(spit f (cljfmt/reformat-string project (slurp f)))
                 :file file-path}))
      (doall invalid-files)))))
