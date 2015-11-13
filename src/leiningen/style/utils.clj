(ns leiningen.style.utils
  (:require [leiningen.core.main :as main]))


(defn print-help []
  (main/abort "USAGE: lein style [COMMAND] LIBRARY\n\n - COMMAND: [check | fix | coverage | help] default: help\n - LIBRARY: [cljfmt | kibit | eastwood | cloverage | all] default: all\n"))

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
