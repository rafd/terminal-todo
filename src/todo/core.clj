(ns todo.core
  (:require 
    [reterm.core :as r]
    [todo.views :refer [app-view]]
    [todo.transact :as tx]))

(defn -main []
  (let [mode (get [:swing :text] 0)]
    (tx/init!)
    (r/render mode [app-view])))
