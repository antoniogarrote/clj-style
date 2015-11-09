(ns leiningen.style.fix
  (:require [leiningen.style.cljfmt :as cljfmt]
            [leiningen.style.utils :refer [print-help]]))

(defmulti fix (fn [project library args] library))

(defmethod fix :cljfmt
  [project _ args]
  (apply cljfmt/fix project args))

(defmethod fix :all
  [project _ args]
  (fix project :cljfmt args))

(defmethod fix :default [_ _ _] (print-help))
