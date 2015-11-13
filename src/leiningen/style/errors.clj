(ns leiningen.style.errors
  (:require [leiningen.style.printing :refer [colorize]]))

(defn make-error [file source description text]
  {:file file
   :source source
   :description description
   :text text})
