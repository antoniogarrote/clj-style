(ns leiningen.style.utils
  (:require [leiningen.core.main :as main]))

(def ansi-colors
  {:reset  "[0m"
   :red    "[031m"
   :green  "[032m"
   :cyan   "[036m"
   :yellow "[33m"})

(defn colorize [s color]
  (str \u001b (ansi-colors color) s \u001b (ansi-colors :reset)))

(defn print-help []
  (main/abort "USAGE: lein style [COMMAND] LIBRARY\n\n - COMMAND: [check | fix | help] default: help\n - LIBRARY: [cljfmt | kibit | eastwood | all] default: all\n"))

(defn print-progress
  ([status] (print (colorize "." (if status :green :red))) (flush)))

(defmacro with-muted-output
  "Evaluates exprs in a context in which *out* and *err* ire bound to a fresh
  StringWriter.  Returns the string created by any nested printing
  calls."
  {:added "1.0"}
  [& body]
  `(let [s# (new java.io.StringWriter)]
     (binding [*out* s# *err* s#]
       (let [res# (do ~@body)]
         res#))))
