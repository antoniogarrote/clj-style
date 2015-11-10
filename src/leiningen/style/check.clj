(ns leiningen.style.check
  (:require [leiningen.style.cljfmt :as cljfmt]
            [leiningen.style.kibit :as kibit]
            [leiningen.style.eastwood :as eastwood]
            [leiningen.style.cloverage :as cloverage]
            [leiningen.style.utils :refer [print-help]]))

(defmulti check (fn [project library args] library))

(defmethod check :cljfmt
  [project _ args]
  (apply cljfmt/check project args))

(defmethod check :kibit
  [project _ args]
  (apply kibit/check project args))

(defmethod check :eastwood
  [project _ args]
  (apply eastwood/check project args))

(defmethod check :cloverage
  [project _ args]
  (apply cloverage/check project args))

(defmethod check :all
  [project _ args]
  (apply concat
         (pmap #(check project % args) [:cljfmt :kibit :eastwood :cloverage])))

(defmethod check :default [_ _ _] (print-help))
