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
