(ns leiningen.style.errors
  (:require [leiningen.core.main :as main]
            [leiningen.style.utils :refer [colorize]]))

(defn make-error [file source description text]
  {:file file
   :source source
   :description description
   :text text})

(defn display-error [{:keys [file source description text]}]
  (main/warn (str (colorize "* Error: " :yellow) (colorize description :cyan)))
  (main/warn (str (colorize "- File: " :yellow) file))
  (main/warn (str (colorize "- Source: " :yellow) source))
  (main/warn (str (colorize "- Description:\n" :yellow) text "\n")))

(defn apply-fix [{:keys [file error fix]}]
  (display-error error)
  (colorize (str "* Fixing " file) :green)
  (fix))
