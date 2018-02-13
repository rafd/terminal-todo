(ns todo.transact
  (:require
    [reterm.core :as r]
    [reterm.state :as rs]
    [todo.state :refer [state]]
    [todo.generate :refer [generate-state]]))

(defn update-task-description! 
  [id description]
  (swap! state assoc-in [:tasks id :description] description))

(defn init! []
  (reset! state (generate-state)))

; TODO should find a better way to do this
(def cursor-up! rs/cursor-up!)
(def cursor-down! rs/cursor-down!)
(def cursor-left! rs/cursor-left!)
(def cursor-right! rs/cursor-right!)
(def stop! r/stop!)
