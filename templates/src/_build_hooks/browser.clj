(ns _build_hooks.browser
  (:require [clojure.java.shell :as sh]))

(defn copy-index
  {:shadow.build/stage :flush}
  [state & args]
  (sh/sh "cp" "src/_build_hooks/index.html" "build/browser")
  state)
