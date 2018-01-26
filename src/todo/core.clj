(ns todo.core
  (:require 
    [reterm.core :as r]
    [todo.views :refer [app-view]]))

(defn -main []
  (let [mode (get [:swing :text] 0)]
    (r/render mode [app-view])))
