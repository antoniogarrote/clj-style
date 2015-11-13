(ns leiningen.style.printing)

(def ansi-colors
  {:reset  "[0m"
   :red    "[031m"
   :green  "[032m"
   :cyan   "[036m"
   :yellow "[33m"})

(defn colorize [s color]
  (str \u001b (ansi-colors color) s \u001b (ansi-colors :reset)))

(defn print-progress
  ([status] (print (colorize "." (if status :green :red))) (flush)))
